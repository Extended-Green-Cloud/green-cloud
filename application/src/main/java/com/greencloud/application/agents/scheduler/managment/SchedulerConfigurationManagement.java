package com.greencloud.application.agents.scheduler.managment;

import static com.greencloud.application.agents.scheduler.managment.logs.SchedulerManagementLog.INCREASE_DEADLINE_WEIGHT_LOG;
import static com.greencloud.application.agents.scheduler.managment.logs.SchedulerManagementLog.INCREASE_POWER_WEIGHT_LOG;
import static com.greencloud.application.utils.AlgorithmUtils.nextFibonacci;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.commons.job.ClientJob;

/**
 * Set of utilities used to manage the configuration of scheduler agent
 */
public class SchedulerConfigurationManagement {

	private static final Logger logger = LoggerFactory.getLogger(SchedulerConfigurationManagement.class);
	private int underlyingDeadlineWeight;
	private int underlyingPowerWeight;
	private double deadlineWeightPriority;
	private double powerWeightPriority;
	private int maximumQueueSize;
	private int jobSplitThreshold;
	private int splittingFactor;

	/**
	 * Constructor
	 *
	 * @param deadlineWeightPriority initial weight of deadline priority
	 * @param powerWeightPriority    initial weight of power priority
	 * @param maximumQueueSize       maximum queue size
	 *                               //@param jobSplitThreshold	 job size at which splitting will be triggered, can be adjusted by the ManagingAgent
	 */
	public SchedulerConfigurationManagement(int deadlineWeightPriority, int powerWeightPriority, int maximumQueueSize,
			int jobSplitThreshold, int splittingFactor) {
		this.underlyingDeadlineWeight = deadlineWeightPriority;
		this.underlyingPowerWeight = powerWeightPriority;
		this.maximumQueueSize = maximumQueueSize;
		this.jobSplitThreshold = jobSplitThreshold;
		this.splittingFactor = splittingFactor;
		computeWeights();
	}

	/**
	 * Method computes the priority for the given job
	 *
	 * @param clientJob job of interest
	 * @return double being the job priority
	 */
	public double getJobPriority(final ClientJob clientJob) {
		return deadlineWeightPriority * getTimeToDeadline(clientJob) + powerWeightPriority * clientJob.getPower();
	}

	private double getTimeToDeadline(final ClientJob clientJob) {
		return Duration.between(clientJob.getEndTime(), clientJob.getDeadline()).toMillis();
	}

	public int getMaximumQueueSize() {
		return maximumQueueSize;
	}

	public int getJobSplitThreshold() {
		return jobSplitThreshold;
	}

	public int getSplittingFactor() {
		return splittingFactor;
	}

	public double getDeadlineWeightPriority() {
		return deadlineWeightPriority;
	}

	public double getPowerWeightPriority() {
		return powerWeightPriority;
	}

	/**
	 * Method increases the deadline weight to the next number in a Fibonacci sequence
	 */
	public boolean increaseDeadlineWeight() {
		int oldWeight = underlyingDeadlineWeight;
		underlyingDeadlineWeight = nextFibonacci(underlyingDeadlineWeight);
		logger.info(INCREASE_DEADLINE_WEIGHT_LOG, oldWeight, underlyingDeadlineWeight);
		computeWeights();
		return true;
	}

	/**
	 * Method increases the power weight to the next number in a Fibonacci sequence
	 */
	public boolean increasePowerWeight() {
		int oldWeight = underlyingPowerWeight;
		underlyingPowerWeight = nextFibonacci(underlyingPowerWeight);
		logger.info(INCREASE_POWER_WEIGHT_LOG, oldWeight, underlyingPowerWeight);
		computeWeights();
		return true;
	}

	private void computeWeights() {
		this.deadlineWeightPriority =
				(double) underlyingDeadlineWeight / (underlyingDeadlineWeight + underlyingPowerWeight);
		this.powerWeightPriority = (double) underlyingPowerWeight / (underlyingDeadlineWeight + underlyingPowerWeight);
	}
}
