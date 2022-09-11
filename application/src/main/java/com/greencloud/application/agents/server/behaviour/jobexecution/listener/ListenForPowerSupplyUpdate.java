package com.greencloud.application.agents.server.behaviour.jobexecution.listener;

import static com.greencloud.application.agents.server.behaviour.jobexecution.listener.logs.JobHandlingListenerLog.SUPPLY_CONFIRMATION_JOB_ANNOUNCEMENT_LOG;
import static com.greencloud.application.agents.server.behaviour.jobexecution.listener.logs.JobHandlingListenerLog.SUPPLY_CONFIRMATION_JOB_SCHEDULING_LOG;
import static com.greencloud.application.agents.server.behaviour.jobexecution.listener.logs.JobHandlingListenerLog.SUPPLY_FINISHED_MANUALLY_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.MANUAL_JOB_FINISH_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.SERVER_JOB_CFP_PROTOCOL;
import static com.greencloud.application.utils.GUIUtils.announceBookedJob;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.agents.server.behaviour.jobexecution.handler.HandleJobStart;
import com.greencloud.application.agents.server.behaviour.jobexecution.listener.logs.JobHandlingListenerLog;
import com.greencloud.application.agents.server.behaviour.jobexecution.listener.templates.JobHandlingMessageTemplates;
import com.greencloud.application.domain.job.Job;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.domain.job.JobStatusEnum;
import com.greencloud.application.exception.IncorrectMessageContentException;
import com.greencloud.application.messages.MessagingUtils;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for power supply update messages coming from Green Source
 */
public class ListenForPowerSupplyUpdate extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForPowerSupplyUpdate.class);

	private ServerAgent myServerAgent;

	/**
	 * Method casts the abstract agent to agent of type Server Agent
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myServerAgent = (ServerAgent) myAgent;
	}

	/**
	 * Method listens for the messages coming from Green Source with updates regarding power supply.
	 * It handles two types of messages:
	 *
	 * - messages confirming that given green source will provide power necessary to supply the job (either new job
	 * or transferred one)
	 * - messages informing that the job execution was finished manually as the message about job finish did not come
	 * on time
	 */
	@Override
	public void action() {
		final ACLMessage inform = myAgent.receive(JobHandlingMessageTemplates.POWER_SUPPLY_UPDATE_TEMPLATE);

		if (Objects.nonNull(inform)) {
			switch (inform.getProtocol()) {
				case MANUAL_JOB_FINISH_PROTOCOL -> handlePowerSupplyManualFinishMessage(inform);
				default -> handlePowerConfirmationMessage(inform);
			}
		} else {
			block();
		}
	}

	private void handlePowerSupplyManualFinishMessage(final ACLMessage inform) {
		final Job job = retrieveJobFromMessage(inform);
		final JobStatusEnum statusEnum = isNull(job) ? null : myServerAgent.getServerJobs().getOrDefault(job, null);

		if (nonNull(statusEnum) && statusEnum.equals(JobStatusEnum.IN_PROGRESS)) {
			MDC.put(MDC_JOB_ID, job.getJobId());
			logger.debug(SUPPLY_FINISHED_MANUALLY_LOG, job.getClientIdentifier(), job.getClientIdentifier());
			myServerAgent.manage().finishJobExecution(job, true);
		}
	}

	private void handlePowerConfirmationMessage(final ACLMessage inform) {
		final JobInstanceIdentifier jobInstanceId = MessagingUtils.readMessageContent(inform,
				JobInstanceIdentifier.class);
		final String messageType = inform.getProtocol();
		final String jobId = jobInstanceId.getJobId();

		if (messageType.equals(SERVER_JOB_CFP_PROTOCOL)) {
			MDC.put(MDC_JOB_ID, jobId);
			logger.info(SUPPLY_CONFIRMATION_JOB_ANNOUNCEMENT_LOG, jobId);
			announceBookedJob(myServerAgent);
		}
		scheduleJobExecution(jobInstanceId, messageType);
	}

	private void scheduleJobExecution(final JobInstanceIdentifier jobInstanceId, final String messageType) {
		final Job job = myServerAgent.manage().getJobByIdAndStartDate(jobInstanceId);

		if (nonNull(job)) {
			MDC.put(MDC_JOB_ID, job.getJobId());
			logger.info(SUPPLY_CONFIRMATION_JOB_SCHEDULING_LOG, jobInstanceId.getJobId());
			final boolean informCNAStart = messageType.equals(SERVER_JOB_CFP_PROTOCOL) || jobInstanceId.getStartTime()
					.isAfter(getCurrentTime());
			myAgent.addBehaviour(HandleJobStart.createFor(myServerAgent, job, informCNAStart, true));
		} else {
			logger.info(JobHandlingListenerLog.SUPPLY_CONFIRMATION_JOB_FINISHED_LOG, jobInstanceId.getJobId());
		}
	}

	private Job retrieveJobFromMessage(final ACLMessage inform) {
		try {
			final String jobId = MessagingUtils.readMessageContent(inform, String.class);
			return myServerAgent.manage().getJobById(jobId);
		} catch (IncorrectMessageContentException e) {
			final JobInstanceIdentifier identifier = MessagingUtils.readMessageContent(inform, JobInstanceIdentifier.class);
			return myServerAgent.manage().getJobByIdAndStartDate(identifier);
		}
	}

}