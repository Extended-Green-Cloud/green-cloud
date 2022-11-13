package com.greencloud.application.agents.scheduler.managment;

import static com.greencloud.application.agents.scheduler.domain.SchedulerAgentConstants.JOB_RETRY_MINUTES_ADJUSTMENT;
import static com.greencloud.application.agents.scheduler.managment.logs.SchedulerManagementLog.FULL_JOBS_QUEUE_LOG;
import static com.greencloud.application.common.constant.LoggingConstant.MDC_JOB_ID;
import static com.greencloud.application.domain.job.JobStatusEnum.CREATED;
import static com.greencloud.application.mapper.JobMapper.mapToJobWithNewTime;
import static com.greencloud.application.utils.TimeUtils.postponeTime;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.scheduler.SchedulerAgent;
import com.greencloud.application.agents.scheduler.behaviour.jobscheduling.initiator.InitiateCNALookup;
import com.greencloud.application.domain.job.ClientJob;

/**
 * Set of utilities used to manage the state of scheduler agent
 */
public class SchedulerStateManagement {

	private static final Logger logger = LoggerFactory.getLogger(InitiateCNALookup.class);
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
	 * Method retrieves the job based on the given id
	 *
	 * @param jobId unique job identifier
	 * @return Job
	 */
	public ClientJob getJobById(final String jobId) {
		return schedulerAgent.getClientJobs().keySet().stream()
				.filter(job -> job.getJobId().equals(jobId))
				.findFirst()
				.orElse(null);
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
		schedulerAgent.getClientJobs().remove(job);
		schedulerAgent.getClientJobs().put(adjustedJob, CREATED);

		if (!schedulerAgent.getJobsToBeExecuted().offer(adjustedJob)) {
			MDC.put(MDC_JOB_ID, job.getJobId());
			logger.info(FULL_JOBS_QUEUE_LOG, job.getJobId());
		}
		return true;
	}

	private boolean isJobAfterDeadline(final ClientJob job) {
		final Instant endAfterPostpone = postponeTime(job.getEndTime(), JOB_RETRY_MINUTES_ADJUSTMENT);
		return endAfterPostpone.isAfter(job.getDeadline());
	}
}