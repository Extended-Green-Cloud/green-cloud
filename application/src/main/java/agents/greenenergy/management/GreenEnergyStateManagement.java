package agents.greenenergy.management;

import static agents.greenenergy.domain.GreenEnergyAgentConstants.MAX_ERROR_IN_JOB_FINISH;
import static agents.greenenergy.management.logs.GreenEnergyManagementLog.AVERAGE_POWER_LOG;
import static agents.greenenergy.management.logs.GreenEnergyManagementLog.CURRENT_AVAILABLE_POWER_LOG;
import static agents.greenenergy.management.logs.GreenEnergyManagementLog.DUPLICATED_POWER_JOB_FINISH_LOG;
import static agents.greenenergy.management.logs.GreenEnergyManagementLog.DUPLICATED_POWER_JOB_START_LOG;
import static agents.greenenergy.management.logs.GreenEnergyManagementLog.UNIQUE_POWER_JOB_FINISH_LOG;
import static agents.greenenergy.management.logs.GreenEnergyManagementLog.UNIQUE_POWER_JOB_START_LOG;
import static domain.job.JobStatusEnum.ACCEPTED_JOB_STATUSES;
import static domain.job.JobStatusEnum.ACTIVE_JOB_STATUSES;
import static domain.job.JobStatusEnum.JOB_ON_HOLD;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import static utils.TimeUtils.getCurrentTime;
import static utils.TimeUtils.isWithinTimeStamp;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gui.agents.GreenEnergyAgentNode;

import agents.greenenergy.GreenEnergyAgent;
import agents.greenenergy.behaviour.powersupply.handler.HandleManualPowerSupplyFinish;
import domain.MonitoringData;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import domain.job.PowerJob;
import mapper.JobMapper;

/**
 * Set of methods used to manage the internal state of the green energy agent
 */
public class GreenEnergyStateManagement {

	private static final Logger logger = LoggerFactory.getLogger(GreenEnergyStateManagement.class);
	protected final AtomicInteger uniqueStartedJobs;
	protected final AtomicInteger uniqueFinishedJobs;
	protected final AtomicInteger startedJobsInstances;
	protected final AtomicInteger finishedJobsInstances;
	private final GreenEnergyAgent greenEnergyAgent;

	/**
	 * Default constructor
	 *
	 * @param greenEnergyAgent - agent representing given source
	 */
	public GreenEnergyStateManagement(GreenEnergyAgent greenEnergyAgent) {
		this.greenEnergyAgent = greenEnergyAgent;
		this.uniqueStartedJobs = new AtomicInteger(0);
		this.uniqueFinishedJobs = new AtomicInteger(0);
		this.startedJobsInstances = new AtomicInteger(0);
		this.finishedJobsInstances = new AtomicInteger(0);
	}

	/**
	 * Method retrieves the job by the job id from job map
	 *
	 * @param jobId job identifier
	 * @return job or null if job is not found
	 */
	public PowerJob getJobById(final String jobId) {
		return greenEnergyAgent.getPowerJobs().keySet().stream()
				.filter(job -> job.getJobId().equals(jobId))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Method retrieves the job by the job id and start time from job map
	 *
	 * @param jobId     job identifier
	 * @param startTime job start time
	 * @return job
	 */
	public PowerJob getJobByIdAndStartDate(final String jobId, final Instant startTime) {
		return greenEnergyAgent.getPowerJobs().keySet().stream()
				.filter(job -> job.getJobId().equals(jobId) && job.getStartTime().equals(startTime))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Method retrieves the job by the job id and start time from job map
	 *
	 * @param jobInstanceId unique identifier of the job instance
	 * @return job
	 */
	public PowerJob getJobByIdAndStartDate(final JobInstanceIdentifier jobInstanceId) {
		return greenEnergyAgent.getPowerJobs().keySet().stream()
				.filter(job -> job.getJobId().equals(jobInstanceId.getJobId())
						&& job.getStartTime().equals(jobInstanceId.getStartTime()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Method increments the count of started jobs
	 *
	 * @param jobId unique job identifier
	 */
	public void incrementStartedJobs(final String jobId) {
		if (isJobUnique(jobId)) {
			uniqueStartedJobs.getAndAdd(1);
			logger.info(UNIQUE_POWER_JOB_START_LOG, greenEnergyAgent.getLocalName(), jobId, uniqueStartedJobs);
		}
		startedJobsInstances.getAndAdd(1);
		logger.info(DUPLICATED_POWER_JOB_START_LOG, greenEnergyAgent.getLocalName(), jobId, startedJobsInstances);
		updateGreenSourceGUI();
	}

	/**
	 * Method increments the count of finished jobs
	 *
	 * @param jobId unique identifier of the job
	 */
	public void incrementFinishedJobs(final String jobId) {
		if (isJobUnique(jobId)) {
			uniqueFinishedJobs.getAndAdd(1);
			logger.info(UNIQUE_POWER_JOB_FINISH_LOG, greenEnergyAgent.getLocalName(), jobId,
					uniqueFinishedJobs, uniqueStartedJobs);
		}
		finishedJobsInstances.getAndAdd(1);
		logger.info(DUPLICATED_POWER_JOB_FINISH_LOG, greenEnergyAgent.getLocalName(), jobId,
				finishedJobsInstances, startedJobsInstances);
		updateGreenSourceGUI();
	}

	/**
	 * Method changes the green source's maximum capacity
	 *
	 * @param newMaximumCapacity new maximum capacity value
	 */
	public void updateMaximumCapacity(final int newMaximumCapacity) {
		greenEnergyAgent.setMaximumCapacity(newMaximumCapacity);
		final GreenEnergyAgentNode greenEnergyAgentNode = (GreenEnergyAgentNode) greenEnergyAgent.getAgentNode();

		if (nonNull(greenEnergyAgentNode)) {
			greenEnergyAgentNode.updateMaximumCapacity(greenEnergyAgent.getMaximumCapacity());
		}
	}

	/**
	 * Method creates new instances for given power job which will be affected by the power shortage.
	 * If the power shortage will begin after the start of job execution -> job will be divided into 2
	 *
	 * Example:
	 * Job1 (start: 08:00, finish: 10:00)
	 * Power shortage start: 09:00
	 *
	 * Job1Instance1: (start: 08:00, finish: 09:00) <- job not affected by power shortage
	 * Job1Instance2: (start: 09:00, finish: 10:00) <- job affected by power shortage
	 *
	 * @param powerJob           affected power job
	 * @param powerShortageStart time when power shortage starts
	 */
	public PowerJob dividePowerJobForPowerShortage(final PowerJob powerJob, final Instant powerShortageStart) {
		if (powerShortageStart.isAfter(powerJob.getStartTime())) {
			final PowerJob affectedPowerJobInstance = JobMapper.mapToJobNewStartTime(powerJob, powerShortageStart);
			final PowerJob notAffectedPowerJobInstance = JobMapper.mapToJobNewEndTime(powerJob, powerShortageStart);
			final JobStatusEnum currentJobStatus = greenEnergyAgent.getPowerJobs().get(powerJob);

			greenEnergyAgent.getPowerJobs().remove(powerJob);
			greenEnergyAgent.getPowerJobs().put(affectedPowerJobInstance, JobStatusEnum.ON_HOLD_TRANSFER);
			greenEnergyAgent.getPowerJobs().put(notAffectedPowerJobInstance, currentJobStatus);
			final Date endDate = Date.from(
					affectedPowerJobInstance.getEndTime().plusMillis(MAX_ERROR_IN_JOB_FINISH));
			greenEnergyAgent.addBehaviour(
					new HandleManualPowerSupplyFinish(greenEnergyAgent, endDate,
							JobMapper.mapToJobInstanceId(affectedPowerJobInstance)));
			updateGreenSourceGUI();
			return affectedPowerJobInstance;
		} else {
			greenEnergyAgent.getPowerJobs().replace(powerJob, JobStatusEnum.ON_HOLD_TRANSFER);
			updateGreenSourceGUI();
			return powerJob;
		}
	}

	/**
	 * Finds distinct start and end times of taken {@link PowerJob}s including the candidate job
	 *
	 * @param candidateJob job defining the search time window
	 * @return list of all start and end times
	 */
	public List<Instant> getJobsTimetable(PowerJob candidateJob) {
		var validJobs = greenEnergyAgent.getPowerJobs().entrySet().stream()
				.filter(entry -> ACCEPTED_JOB_STATUSES.contains(entry.getValue()))
				.map(Entry::getKey)
				.toList();
		return Stream.concat(
						Stream.of(candidateJob.getStartTime(), candidateJob.getEndTime()),
						Stream.concat(
								validJobs.stream().map(PowerJob::getStartTime),
								validJobs.stream().map(PowerJob::getEndTime)))
				.distinct()
				.toList();
	}

	/**
	 * Computes average power available during computation of the job being processed
	 *
	 * @param powerJob job of interest
	 * @param weather  monitoring data with weather for requested timetable
	 * @param isNewJob flag indicating whether job of interest is a processed new job
	 * @return average available power as decimal or empty optional if power not available
	 */
	public synchronized Optional<Double> getAverageAvailablePower(final PowerJob powerJob,
			final MonitoringData weather, final boolean isNewJob) {
		var powerChart = getPowerChart(powerJob, weather, isNewJob);
		var availablePower = powerChart.values().stream().mapToDouble(a -> a).average().orElse(0.0D);
		var power = String.format("%.2f", availablePower);

		logger.info(AVERAGE_POWER_LOG, greenEnergyAgent.getName(), greenEnergyAgent.getEnergyType(), power,
				powerJob.getStartTime(), powerJob.getEndTime());

		return powerChart.values().stream().anyMatch(value -> value <= 0) ?
				Optional.empty() :
				Optional.of(availablePower);
	}

	/**
	 * Computes available power available in the given moment
	 *
	 * @param time    time of the check
	 * @param weather monitoring data with weather for requested timetable
	 * @return average available power as decimal or empty optional if power not available
	 */
	public synchronized Optional<Double> getAvailablePower(final Instant time, final MonitoringData weather) {
		var availablePower = getPower(time, weather);
		var power = String.format("%.2f", availablePower);

		logger.info(CURRENT_AVAILABLE_POWER_LOG, greenEnergyAgent.getName(), greenEnergyAgent.getEnergyType(), power,
				time);

		return Optional.of(availablePower).filter(powerVal -> powerVal >= 0.0);
	}

	/**
	 * Method updates the information on the green source GUI
	 */
	public void updateGreenSourceGUI() {
		final GreenEnergyAgentNode greenEnergyAgentNode = (GreenEnergyAgentNode) greenEnergyAgent.getAgentNode();

		if (nonNull(greenEnergyAgentNode)) {
			greenEnergyAgentNode.updateMaximumCapacity(greenEnergyAgent.getMaximumCapacity());
			greenEnergyAgentNode.updateJobsCount(getJobCount());
			greenEnergyAgentNode.updateJobsOnHoldCount(getOnHoldJobCount());
			greenEnergyAgentNode.updateIsActive(getIsActiveState());
			greenEnergyAgentNode.updateTraffic(getCurrentPowerInUseForGreenSource());
		}
	}

	public AtomicInteger getUniqueStartedJobs() {
		return uniqueStartedJobs;
	}

	public AtomicInteger getUniqueFinishedJobs() {
		return uniqueFinishedJobs;
	}

	public AtomicInteger getStartedJobsInstances() {
		return startedJobsInstances;
	}

	public AtomicInteger getFinishedJobsInstances() {
		return finishedJobsInstances;
	}

	private synchronized Double getPower(Instant start, MonitoringData weather) {
		final double inUseCapacity = greenEnergyAgent.getPowerJobs().keySet().stream()
				.filter(job -> ACCEPTED_JOB_STATUSES.contains(greenEnergyAgent.getPowerJobs().get(job)) &&
						job.isExecutedAtTime(start))
				.mapToInt(PowerJob::getPower)
				.sum();
		return greenEnergyAgent.getCapacity(weather, start) - inUseCapacity;
	}

	private synchronized Map<Instant, Double> getPowerChart(PowerJob powerJob, final MonitoringData weather,
			final boolean isNewJob) {
		var start = powerJob.getStartTime();
		var end = powerJob.getEndTime();
		var jobStatuses = isNewJob ? ACCEPTED_JOB_STATUSES : ACTIVE_JOB_STATUSES;

		var timetable = getJobsTimetable(powerJob).stream()
				.filter(time -> isWithinTimeStamp(start, end, time))
				.toList();
		var powerJobs = greenEnergyAgent.getPowerJobs().keySet().stream()
				.filter(job -> jobStatuses.contains(greenEnergyAgent.getPowerJobs().get(job)))
				.toList();

		if (powerJobs.isEmpty()) {
			return timetable.stream()
					.collect(toMap(Function.identity(), time -> greenEnergyAgent.getCapacity(weather, time)));
		}

		return timetable.stream()
				.collect(toMap(Function.identity(), time ->
						powerJobs.stream()
								.filter(job -> job.isExecutedAtTime(time))
								.map(PowerJob::getPower)
								.map(power -> greenEnergyAgent.getCapacity(weather, time) - power)
								.mapToDouble(a -> a)
								.average()
								.orElseGet(() -> 0.0)));
	}

	private boolean isJobUnique(final String jobId) {
		return greenEnergyAgent.getPowerJobs().keySet().stream()
				.filter(job -> job.getJobId().equals(jobId))
				.toList()
				.size()
				== 1;
	}

	public int getCurrentPowerInUseForGreenSource() {
		return greenEnergyAgent.getPowerJobs().entrySet().stream()
				.filter(job -> job.getValue().equals(JobStatusEnum.IN_PROGRESS)
						&& isWithinTimeStamp(job.getKey().getStartTime(), job.getKey().getEndTime(), getCurrentTime()))
				.mapToInt(job -> job.getKey().getPower())
				.sum();
	}

	private int getOnHoldJobCount() {
		return greenEnergyAgent.getPowerJobs().entrySet().stream()
				.filter(job -> JOB_ON_HOLD.contains(job.getValue())
						&& isWithinTimeStamp(
						job.getKey().getStartTime(), job.getKey().getEndTime(), getCurrentTime()))
				.toList()
				.size();
	}

	private int getJobCount() {
		return greenEnergyAgent.getPowerJobs().entrySet().stream()
				.filter(job -> ACCEPTED_JOB_STATUSES.contains(job.getValue())
						&& isWithinTimeStamp(
						job.getKey().getStartTime(), job.getKey().getEndTime(), getCurrentTime()))
				.map(Map.Entry::getKey)
				.map(PowerJob::getJobId)
				.collect(Collectors.toSet())
				.size();
	}

	private boolean getIsActiveState() {
		return getCurrentPowerInUseForGreenSource() > 0 || getOnHoldJobCount() > 0;
	}
}