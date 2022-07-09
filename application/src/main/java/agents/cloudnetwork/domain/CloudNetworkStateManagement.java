package agents.cloudnetwork.domain;

import static common.GUIUtils.announceFinishedJob;
import static domain.job.JobStatusEnum.JOB_IN_PROGRESS;
import static domain.job.JobStatusEnum.PROCESSING;

import agents.cloudnetwork.CloudNetworkAgent;
import com.gui.domain.nodes.CloudNetworkAgentNode;
import domain.job.Job;
import domain.job.JobStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

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
                .mapToInt(job -> job.getKey().getPower()).sum();
    }

    /**
     * Method retrieves the job by the job id from job map
     *
     * @param jobId job identifier
     * @return job
     */
    public Job getJobById(final String jobId) {
        return cloudNetworkAgent.getNetworkJobs().keySet().stream().filter(job -> job.getJobId().equals(jobId)).findFirst().orElse(null);
    }

    /**
     * Method increments the count of started jobs
     *
     * @param jobId unique job identifier
     */
    public void incrementStartedJobs(final String jobId) {
        startedJobs.getAndAdd(1);
        logger.info("[{}] Started job {}. Number of started jobs is {}", cloudNetworkAgent.getLocalName(), jobId, startedJobs);
        cloudNetworkAgent.getGuiController().updateActiveJobsCountByValue(1);
        updateCloudNetworkGUI();
    }

    /**
     * Method increments the count of finished jobs
     *
     * @param jobId unique identifier of the job
     */
    public void incrementFinishedJobs(final String jobId) {
        finishedJobs.getAndAdd(1);
        logger.info("[{}] Finished job {}. Number of finished jobs is {} out of {} started", cloudNetworkAgent.getLocalName(), jobId, finishedJobs, startedJobs);
        updateCloudNetworkGUI();
        announceFinishedJob(cloudNetworkAgent, jobId);
    }

    private void updateCloudNetworkGUI() {
        final CloudNetworkAgentNode cloudNetworkAgentNode = (CloudNetworkAgentNode) cloudNetworkAgent.getAgentNode();
        cloudNetworkAgentNode.updateClientNumber(getScheduledJobs());
        cloudNetworkAgentNode.updateJobsCount(getJobInProgressCount());
        cloudNetworkAgentNode.updateTraffic(getCurrentPowerInUse());
    }

    private int getJobInProgressCount() {
        return cloudNetworkAgent.getNetworkJobs().entrySet().stream()
                .filter(job -> JOB_IN_PROGRESS.contains(job.getValue()))
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