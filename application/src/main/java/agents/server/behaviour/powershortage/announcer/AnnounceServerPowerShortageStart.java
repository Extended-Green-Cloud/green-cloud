package agents.server.behaviour.powershortage.announcer;

import static agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_START_DETECTED_LOG;
import static agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_START_NO_IMPACT_LOG;
import static agents.server.behaviour.powershortage.announcer.logs.PowerShortageServerAnnouncerLog.POWER_SHORTAGE_START_TRANSFER_REQUEST_LOG;
import static domain.job.JobStatusEnum.ACTIVE_JOB_STATUSES;
import static messages.domain.constants.MessageProtocolConstants.SERVER_POWER_SHORTAGE_ALERT_PROTOCOL;
import static messages.domain.factory.PowerShortageMessageFactory.prepareJobPowerShortageInformation;
import static messages.domain.factory.PowerShortageMessageFactory.preparePowerShortageTransferRequest;
import static utils.AlgorithmUtils.findJobsWithinPower;
import static utils.GUIUtils.displayMessageArrow;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.server.ServerAgent;
import agents.server.behaviour.powershortage.handler.HandleServerPowerShortage;
import agents.server.behaviour.powershortage.initiator.InitiateJobTransferInCloudNetwork;
import common.mapper.JobMapper;
import domain.job.Job;
import domain.powershortage.PowerShortageJob;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * Behaviour sends the information to the Cloud Network Agent (CNA) that the power shortage has occurred
 */
public class AnnounceServerPowerShortageStart extends OneShotBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(AnnounceServerPowerShortageStart.class);

	private final ServerAgent myServerAgent;
	private final String guid;
	private final Instant powerShortageStartTime;
	private final int recalculatedAvailablePower;

	/**
	 * Behaviour constructor
	 *
	 * @param myAgent                    agent executing the behaviour
	 * @param powerShortageStartTime     start time when the power shortage will begin
	 * @param recalculatedAvailablePower maximum power available during the power shortage
	 */
	public AnnounceServerPowerShortageStart(final ServerAgent myAgent, final Instant powerShortageStartTime,
			final int recalculatedAvailablePower) {
		super(myAgent);
		this.powerShortageStartTime = powerShortageStartTime;
		this.recalculatedAvailablePower = recalculatedAvailablePower;
		this.myServerAgent = myAgent;
		this.guid = myAgent.getName();
	}

	/**
	 * Method is responsible for announcing to the cloud network that there will be some power shortage
	 * which cannot be handled by the server itself
	 */
	@Override
	public void action() {
		logger.info(POWER_SHORTAGE_START_DETECTED_LOG, guid, powerShortageStartTime);
		final List<Job> affectedJobs = getAffectedPowerJobs();

		if (affectedJobs.isEmpty()) {
			logger.info(POWER_SHORTAGE_START_NO_IMPACT_LOG, guid);
			myServerAgent.addBehaviour(
					HandleServerPowerShortage.createFor(Collections.emptyList(), powerShortageStartTime, myServerAgent,
							recalculatedAvailablePower));
		} else {
			final List<Job> jobsToKeep = findJobsWithinPower(affectedJobs, recalculatedAvailablePower, Job.class);
			final List<Job> jobsToTransfer = affectedJobs.stream().filter(job -> !jobsToKeep.contains(job)).toList();

			jobsToTransfer.forEach(job -> {
				logger.info(POWER_SHORTAGE_START_TRANSFER_REQUEST_LOG, guid, job.getJobId());
				final Job jobToTransfer = myServerAgent.manage().divideJobForPowerShortage(job, powerShortageStartTime);
				final PowerShortageJob originalJobForShortage = JobMapper.mapToPowerShortageJob(job,
						powerShortageStartTime);
				final PowerShortageJob jobTransferForShortage = JobMapper.mapToPowerShortageJob(jobToTransfer,
						powerShortageStartTime);

				requestJobTransferInCloudNetwork(originalJobForShortage, jobTransferForShortage);
				informGreenSourceAboutPowerShortage(originalJobForShortage);
			});
			myServerAgent.addBehaviour(
					HandleServerPowerShortage.createFor(jobsToTransfer, powerShortageStartTime, myServerAgent,
							recalculatedAvailablePower));
		}
	}

	private void requestJobTransferInCloudNetwork(final PowerShortageJob originalJob,
			final PowerShortageJob jobToTransfer) {
		final AID cloudNetwork = myServerAgent.getOwnerCloudNetworkAgent();
		final ACLMessage transferMessage = preparePowerShortageTransferRequest(originalJob, cloudNetwork);
		final Behaviour transferRequest = new InitiateJobTransferInCloudNetwork(myServerAgent, transferMessage, null,
				jobToTransfer);

		displayMessageArrow(myServerAgent, cloudNetwork);
		myServerAgent.addBehaviour(transferRequest);
	}

	private void informGreenSourceAboutPowerShortage(final PowerShortageJob originalJob) {
		final AID greenSource = myServerAgent.getGreenSourceForJobMap().get(originalJob.getJobInstanceId().getJobId());
		final ACLMessage powerShortageInformation = prepareJobPowerShortageInformation(originalJob, greenSource,
				SERVER_POWER_SHORTAGE_ALERT_PROTOCOL);

		displayMessageArrow(myServerAgent, greenSource);
		myServerAgent.send(powerShortageInformation);
	}

	private List<Job> getAffectedPowerJobs() {
		return myServerAgent.getServerJobs().keySet().stream()
				.filter(job -> powerShortageStartTime.isBefore(job.getEndTime()) && ACTIVE_JOB_STATUSES.contains(
						myServerAgent.getServerJobs().get(job))).toList();
	}
}