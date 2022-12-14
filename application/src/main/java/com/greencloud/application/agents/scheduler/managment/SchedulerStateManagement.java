package com.greencloud.application.agents.scheduler.managment;

import static com.greencloud.application.agents.scheduler.behaviour.job.scheduling.listener.logs.JobSchedulingListenerLog.JOB_CANCELLATION_LOG;
import static com.greencloud.application.agents.scheduler.domain.SchedulerAgentConstants.JOB_RETRY_MINUTES_ADJUSTMENT;
import static com.greencloud.application.agents.scheduler.managment.logs.SchedulerManagementLog.FULL_JOBS_QUEUE_LOG;
import static com.greencloud.application.agents.scheduler.managment.logs.SchedulerManagementLog.JOB_TIME_ADJUSTED_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.mapper.JobMapper.mapToClientJobRealTime;
import static com.greencloud.commons.job.ExecutionJobStatusEnum.CREATED;
import static com.greencloud.application.mapper.JobMapper.mapToJobWithNewTime;
import static com.greencloud.application.utils.TimeUtils.postponeTime;
import static com.greencloud.commons.job.ExecutionJobStatusEnum.PROCESSING;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.greencloud.application.agents.scheduler.behaviour.job.cancellation.InitiateJobCancellation;
import com.greencloud.commons.job.ExecutionJobStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.scheduler.SchedulerAgent;
import com.greencloud.commons.job.ClientJob;
import com.gui.agents.SchedulerAgentNode;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;

/**
 * Set of utilities used to manage the state of scheduler agent
 */
public class SchedulerStateManagement {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerStateManagement.class);
	private final SchedulerAgent schedulerAgent;

	/**
	 * Default behaviour
	 *
	 * @param schedulerAgent parent scheduler agent
	 */
	public SchedulerStateManagement(final SchedulerAgent schedulerAgent) {
		this.schedulerAgent = schedulerAgent;
	}

	/**
	 * Method postpones the job execution by substituting the previous instance with the one
	 * having adjusted time frames
	 *
	 * @param job job to be postponed
	 * @return true if the operation was successful, false if the job couldn't be postponed due to its deadline
	 */
	public boolean postponeJobExecution(final ClientJob job) {
		if (isJobAfterDeadline(job)) {
			return false;
		}
		final ClientJob adjustedJob = mapToJobWithNewTime(job,
				postponeTime(job.getStartTime(), JOB_RETRY_MINUTES_ADJUSTMENT),
				postponeTime(job.getEndTime(), JOB_RETRY_MINUTES_ADJUSTMENT));
		swapJobInstances(adjustedJob, job);

		if (!schedulerAgent.getJobsToBeExecuted().offer(adjustedJob)) {
			MDC.put(MDC_JOB_ID, job.getJobId());
			logger.info(FULL_JOBS_QUEUE_LOG, job.getJobId());
			updateJobQueue();
		}
		return true;
	}

	/**
	 * Method updates GUI with new job queue
	 */
	public void updateJobQueue() {
		var queueCopy = new LinkedList<>(schedulerAgent.getJobsToBeExecuted());
		var mappedQueue = new LinkedList<ClientJob>();

		queueCopy.iterator().forEachRemaining(el -> mappedQueue.add(mapToClientJobRealTime(el)));
		((SchedulerAgentNode) schedulerAgent.getAgentNode()).updateScheduledJobQueue(mappedQueue);
	}

	/**
	 * Method swaps existing job instance with the new one that has adjusted time frames
	 *
	 * @param newInstance  new job instance
	 * @param prevInstance old job instance
	 */
	public void swapJobInstances(final ClientJob newInstance, final ClientJob prevInstance) {
		schedulerAgent.getClientJobs().remove(prevInstance);
		MDC.put(MDC_JOB_ID, newInstance.getJobId());
		logger.info(JOB_TIME_ADJUSTED_LOG, newInstance.getJobId(), newInstance.getStartTime(),
				newInstance.getEndTime());
		schedulerAgent.getClientJobs().put(newInstance, CREATED);
	}

	/**
	 * Cleans up a job after finished processing
	 *
	 * @param job           job to be cleaned up
	 * @param mainBehaviour if job cancellation have to be initiated it is added as sub-behaviour to the agents main
	 *                      behaviour
	 */
	public void handleFailedJobCleanUp(final ClientJob job, final Behaviour mainBehaviour) {
		final List<String> jobsToRemove = getJobsToRemove(job);
		var originalJobId = job.getJobId().split("#")[0];

		schedulerAgent.getClientJobs().entrySet().removeIf(entry -> jobsToRemove.contains(entry.getKey().getJobId()));
		schedulerAgent.getCnaForJobMap().entrySet().removeIf(entry -> jobsToRemove.contains(entry.getKey()));
		schedulerAgent.getJobParts().entries().removeIf(entry -> entry.getKey().equals(originalJobId)
				&& jobsToRemove.contains(entry.getValue().getJobId()));

		if (schedulerAgent.getClientJobs().keySet().stream().anyMatch(isJobIdEqual(job))) {
			initiateJobCancellation(originalJobId, mainBehaviour);
		}
	}

	public void handleJobCleanUp(final ClientJob job) {
		var originalJobId = job.getJobId().split("#")[0];

		schedulerAgent.getClientJobs().remove(job);
		schedulerAgent.getCnaForJobMap().remove(job.getJobId());
		var jobParts = schedulerAgent.getJobParts().get(originalJobId);
		jobParts.stream()
				.filter(jobPart -> jobPart.getJobId().equals(job.getJobId()))
				.findFirst()
				.ifPresent(jobPart -> schedulerAgent.getJobParts().remove(originalJobId, jobPart));
	}

	private Predicate<ClientJob> isJobIdEqual(final ClientJob job) {
		return jobPart -> job.getJobId().split("#")[0].equals(jobPart.getJobId().split("#")[0]);
	}

	private List<String> getJobsToRemove(final ClientJob job) {
		final Predicate<Map.Entry<ClientJob, ExecutionJobStatusEnum>> shouldRemoveJob =
				jobEntry -> jobEntry.getKey().equals(job) || (isJobIdEqual(job).test(jobEntry.getKey())
						&& List.of(CREATED, PROCESSING).contains(jobEntry.getValue()));

		return schedulerAgent.getClientJobs().entrySet().stream()
				.filter(shouldRemoveJob)
				.map(entry -> entry.getKey().getJobId())
				.toList();
	}

	private void initiateJobCancellation(String originalJobId, Behaviour mainBehaviour) {
		if (schedulerAgent.getFailedJobs().contains(originalJobId)) {
			// do nothing, job cancellation already initiated for that job
			return;
		}

		MDC.put(MDC_JOB_ID, originalJobId);
		logger.info(JOB_CANCELLATION_LOG);
		schedulerAgent.getFailedJobs().add(originalJobId);
		var jobCancellation = InitiateJobCancellation.build(schedulerAgent, originalJobId);
		((ParallelBehaviour) mainBehaviour).addSubBehaviour(jobCancellation);
		MDC.clear();
	}

	private boolean isJobAfterDeadline(final ClientJob job) {
		final Instant endAfterPostpone = postponeTime(job.getEndTime(), JOB_RETRY_MINUTES_ADJUSTMENT);
		return endAfterPostpone.isAfter(job.getDeadline());
	}
}
