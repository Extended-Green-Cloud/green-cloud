package com.greencloud.application.agents.server.behaviour.powershortage.listener;

import static com.greencloud.application.agents.server.behaviour.powershortage.listener.logs.PowerShortageServerListenerLog.GS_SHORTAGE_FINISH_LOG;
import static com.greencloud.application.agents.server.behaviour.powershortage.listener.templates.PowerShortageServerMessageTemplates.SOURCE_POWER_SHORTAGE_FINISH_TEMPLATE;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.domain.job.JobStatusEnum.POWER_SHORTAGE_SOURCE_STATUSES;
import static com.greencloud.application.mapper.JobMapper.mapToJobInstanceId;
import static com.greencloud.application.mapper.JsonMapper.getMapper;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.GREEN_POWER_JOB_PROTOCOL;
import static com.greencloud.application.utils.GUIUtils.displayMessageArrow;
import static com.greencloud.application.utils.TimeUtils.getCurrentTime;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.server.ServerAgent;
import com.greencloud.application.domain.job.ClientJob;
import com.greencloud.application.domain.job.JobInstanceIdentifier;
import com.greencloud.application.domain.job.JobStatusEnum;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour listens for the information that the power shortage in the given green source has finished
 */
public class ListenForSourcePowerShortageFinish extends CyclicBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(ListenForSourcePowerShortageFinish.class);

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
	 * Method listens for the message coming from the Green Source informing that the power
	 * shortage has finished and that given power job can be supplied again using green source power.
	 */
	@Override
	public void action() {
		final ACLMessage inform = myAgent.receive(SOURCE_POWER_SHORTAGE_FINISH_TEMPLATE);

		if (Objects.nonNull(inform)) {
			final ClientJob job = getJobFromMessage(inform);

			if (Objects.nonNull(job) && POWER_SHORTAGE_SOURCE_STATUSES.contains(
					myServerAgent.getServerJobs().get(job))) {
				MDC.put(MDC_JOB_ID, job.getJobId());
				logger.info(GS_SHORTAGE_FINISH_LOG, job.getJobId());
				final AID cloudNetwork = myServerAgent.getOwnerCloudNetworkAgent();

				myServerAgent.getServerJobs().replace(job, getNewJobStatus(job));
				myServerAgent.manage().updateServerGUI();
				displayMessageArrow(myServerAgent, cloudNetwork);
				myServerAgent.manage().informCNAAboutStatusChange(mapToJobInstanceId(job), GREEN_POWER_JOB_PROTOCOL);
			}
		} else {
			block();
		}
	}

	private ClientJob getJobFromMessage(final ACLMessage message) {
		try {
			final JobInstanceIdentifier jobInstanceIdentifier = getMapper().readValue(message.getContent(),
					JobInstanceIdentifier.class);
			return myServerAgent.manage().getJobByIdAndStartDate(jobInstanceIdentifier);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private JobStatusEnum getNewJobStatus(final ClientJob job) {
		return job.getStartTime().isAfter(getCurrentTime()) ?
				JobStatusEnum.ACCEPTED :
				JobStatusEnum.IN_PROGRESS;
	}
}
