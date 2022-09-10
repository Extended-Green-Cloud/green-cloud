package agents.cloudnetwork.management;

import static agents.cloudnetwork.management.logs.CloudNetworkManagementLog.FINISHED_JOB_COUNT_LOG;
import static agents.cloudnetwork.management.logs.CloudNetworkManagementLog.STARTED_JOB_COUNT_LOG;
import static domain.job.JobStatusEnum.ACCEPTED;
import static domain.job.JobStatusEnum.PROCESSING;
import static java.util.Objects.nonNull;
import static utils.GUIUtils.announceFinishedJob;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gui.agents.CloudNetworkAgentNode;

import agents.cloudnetwork.CloudNetworkAgent;
import domain.job.Job;
import domain.job.JobStatusEnum;

/**
 * Set of utilities used to manage the state of the cloud network
 */
public class CloudNetworkStateManagement {

	private static final Logger logger = LoggerFactory.getLogger(CloudNetworkStateManagement.class);

	private final CloudNetworkAgent cloudNetworkAgent;
	protected AtomicInteger startedJobs;
	protected AtomicInteger finishedJobs;

	public CloudNetworkStateManagement(CloudNetworkAgent cloudNetworkAgent) {
		this.cloudNetworkAgent = cloudNetworkAgent;
		this.startedJobs = new AtomicInteger(0);
		this.finishedJobs = new AtomicInteger(0);
	}

	/**
	 * Method calculates the power in use at the given moment
	 *
	 * @return current power in use
	 */
	public int getCurrentPowerInUse() {
		return cloudNetworkAgent.getNetworkJobs().entrySet().stream()
				.filter(job -> job.getValue().equals(JobStatusEnum.IN_PROGRESS))
				.mapToInt(job -> job.getKey().getPower())
				.sum();
	}

	/**
	 * Method retrieves the job by the job id from job map
	 *
	 * @param jobId job identifier
	 * @return job
	 */
	public Job getJobById(final String jobId) {
		return cloudNetworkAgent.getNetworkJobs().keySet().stream()
				.filter(job -> job.getJobId().equals(jobId))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Method increments the count of started jobs
	 *
	 * @param jobId unique job identifier
	 */
	public void incrementStartedJobs(final String jobId) {
		startedJobs.getAndAdd(1);
		logger.info(STARTED_JOB_COUNT_LOG, cloudNetworkAgent.getLocalName(), jobId, startedJobs);

		if (nonNull(cloudNetworkAgent.getGuiController())) {
			cloudNetworkAgent.getGuiController().updateActiveJobsCountByValue(1);
		}
		updateCloudNetworkGUI();
	}

	/**
	 * Method increments the count of finished jobs
	 *
	 * @param jobId unique identifier of the job
	 */
	public void incrementFinishedJobs(final String jobId) {
		finishedJobs.getAndAdd(1);
		logger.info(FINISHED_JOB_COUNT_LOG, cloudNetworkAgent.getLocalName(), jobId, finishedJobs, startedJobs);
		updateCloudNetworkGUI();
		announceFinishedJob(cloudNetworkAgent);
	}

	public AtomicInteger getStartedJobs() {
		return startedJobs;
	}

	public AtomicInteger getFinishedJobs() {
		return finishedJobs;
	}

	private void updateCloudNetworkGUI() {
		final CloudNetworkAgentNode cloudNetworkAgentNode = (CloudNetworkAgentNode) cloudNetworkAgent.getAgentNode();

		if (nonNull(cloudNetworkAgentNode)) {
			cloudNetworkAgentNode.updateClientNumber(getScheduledJobs());
			cloudNetworkAgentNode.updateJobsCount(getJobInProgressCount());
			cloudNetworkAgentNode.updateTraffic(getCurrentPowerInUse());
		}
	}

	private int getJobInProgressCount() {
		final EnumSet<JobStatusEnum> jobStatusesToExclude = EnumSet.of(ACCEPTED, PROCESSING);
		return cloudNetworkAgent.getNetworkJobs().entrySet().stream()
				.filter(job -> !jobStatusesToExclude.contains(job.getValue()))
				.toList()
				.size();
	}

	private int getScheduledJobs() {
		return cloudNetworkAgent.getNetworkJobs().entrySet().stream()
				.filter(job -> !job.getValue().equals(PROCESSING))
				.toList()
				.size();
	}

}