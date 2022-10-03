package com.greencloud.application.agents.greenenergy.behaviour.powersupply.initiator;

import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.initiator.logs.PowerSupplyInitiatorLog.POWER_SUPPLY_PROPOSAL_REJECTED_LOG;
import static com.greencloud.application.agents.greenenergy.behaviour.powersupply.initiator.logs.PowerSupplyInitiatorLog.SEND_POWER_SUPPLY_RESPONSE_LOG;
import static com.greencloud.application.agents.greenenergy.domain.GreenEnergyAgentConstants.MAX_ERROR_IN_JOB_FINISH;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.messages.MessagingUtils.readMessageContent;
import static com.greencloud.application.utils.GUIUtils.displayMessageArrow;
import static com.greencloud.application.utils.JobMapUtils.getJobById;
import static com.greencloud.application.utils.JobMapUtils.getJobByIdAndStartDate;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.util.Objects.isNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.greenenergy.behaviour.powersupply.handler.HandleManualPowerSupplyFinish;
import com.greencloud.application.domain.MonitoringData;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.domain.job.JobStatusEnum;
import com.greencloud.application.domain.job.JobWithProtocol;
import com.greencloud.application.domain.job.PowerJob;
import com.greencloud.application.mapper.JobMapper;
import com.greencloud.application.messages.domain.factory.ReplyMessageFactory;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.ProposeInitiator;

/**
 * Behaviour sends the proposal with power supply offer to Server Agent and handles the retrieved responses.
 */
public class InitiatePowerSupplyOffer extends ProposeInitiator {

	private static final Logger logger = LoggerFactory.getLogger(InitiatePowerSupplyOffer.class);
	private final GreenEnergyAgent myGreenEnergyAgent;
	private final MonitoringData weather;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent   agent which is executing the behaviour
	 * @param msg     proposal message that is sent to the Server Agent
	 * @param weather weather at the time when new job is to be executed
	 */
	public InitiatePowerSupplyOffer(final Agent agent, final ACLMessage msg, final MonitoringData weather) {
		super(agent, msg);
		this.myGreenEnergyAgent = (GreenEnergyAgent) agent;
		this.weather = weather;
	}

	/**
	 * Method handles ACCEPT_PROPOSAL response from server.
	 * It updates the state of the job in green source and replies with the message with correct protocol and the information
	 * that the execution of the given job can be started.
	 *
	 * @param acceptProposal accept proposal response retrieved from the Server Agent
	 */
	@Override
	protected void handleAcceptProposal(final ACLMessage acceptProposal) {
		final JobWithProtocol jobWithProtocol = readMessageContent(acceptProposal, JobWithProtocol.class);
		final PowerJob job = findCorrespondingJob(jobWithProtocol.getJobInstanceIdentifier());
		handleAcceptPowerSupply(job, acceptProposal, jobWithProtocol);
	}

	/**
	 * Method handles REJECT_PROPOSAL response from server.
	 *
	 * @param rejectProposal reject proposal response retrieved from the Server Agent
	 */
	@Override
	protected void handleRejectProposal(final ACLMessage rejectProposal) {
		final JobInstanceIdentifier jobInstanceId = readMessageContent(rejectProposal, JobInstanceIdentifier.class);
		final PowerJob powerJob = getJobByIdAndStartDate(myGreenEnergyAgent.getPowerJobs(), jobInstanceId);
		if (Objects.nonNull(powerJob)) {
			myGreenEnergyAgent.getPowerJobs().remove(powerJob);
		}
		MDC.put(MDC_JOB_ID, jobInstanceId.getJobId());
		logger.info(POWER_SUPPLY_PROPOSAL_REJECTED_LOG);
	}

	private PowerJob findCorrespondingJob(final JobInstanceIdentifier jobInstance) {
		PowerJob job = getJobByIdAndStartDate(myGreenEnergyAgent.getPowerJobs(), jobInstance);
		if (isNull(job)) {
			job = getJobById(myGreenEnergyAgent.getPowerJobs(), jobInstance.getJobId());
		}
		return job;
	}

	private Date calculateExpectedJobEndTime(final PowerJob job) {
		final Instant endDate = getCurrentTime().isAfter(job.getEndTime()) ? getCurrentTime() : job.getEndTime();
		return Date.from(endDate.plus(MAX_ERROR_IN_JOB_FINISH, ChronoUnit.MILLIS));
	}

	private void handleAcceptPowerSupply(final PowerJob job, final ACLMessage acceptProposal,
			final JobWithProtocol jobWithProtocol) {
		myGreenEnergyAgent.getPowerJobs().replace(job, JobStatusEnum.ACCEPTED);
		final Behaviour manualFinishBehaviour = new HandleManualPowerSupplyFinish(myGreenEnergyAgent,
				calculateExpectedJobEndTime(job), JobMapper.mapToJobInstanceId(job));
		myAgent.addBehaviour(manualFinishBehaviour);
		MDC.put(MDC_JOB_ID, job.getJobId());
		logger.info(SEND_POWER_SUPPLY_RESPONSE_LOG, job.getJobId());
		displayMessageArrow(myGreenEnergyAgent, acceptProposal.getSender());
		sendResponseToServer(acceptProposal, jobWithProtocol, INFORM);
	}

	private void sendResponseToServer(final ACLMessage acceptProposal, final JobWithProtocol jobWithProtocol,
			final int protocol) {
		final ACLMessage response = ReplyMessageFactory.prepareReply(acceptProposal.createReply(),
				jobWithProtocol.getJobInstanceIdentifier(), protocol);
		response.setProtocol(jobWithProtocol.getReplyProtocol());
		myAgent.send(response);
	}
}
