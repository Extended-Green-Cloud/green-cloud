package agents.server.management;

import static domain.job.JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY;
import static domain.job.JobStatusEnum.ON_HOLD_TRANSFER;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.quality.Strictness.LENIENT;

import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import agents.AbstractAgent;
import agents.server.ServerAgent;
import agents.server.domain.ServerPowerSourceType;
import agents.server.management.ServerStateManagement;
import domain.GreenSourceData;
import domain.ImmutableGreenSourceData;
import domain.job.ImmutableJob;
import domain.job.ImmutableJobInstanceIdentifier;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.AID;
import utils.TimeUtils;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class ServerStateManagementTest {

	// MOCKED OBJECTS
	private static final Instant MOCK_NOW = parse("2022-01-01T11:00:00.000Z");
	private static final int MOCK_CAPACITY = 200;
	private static final double MOCK_PRICE = 10;
	private static ServerStateManagement MOCK_MANAGEMENT;
	private static Map<Job, JobStatusEnum> MOCK_JOBS;

	@Mock
	private static ServerAgent serverAgent;

	// PARAMETERS USED IN PARAMETRIZED TESTS

	private static Stream<Arguments> parametersGetByIdAndStart() {
		return Stream.of(
				Arguments.of(Instant.parse("2022-01-01T07:30:00.000Z"), "2", true),
				Arguments.of(Instant.parse("2022-01-01T04:30:00.000Z"), "1", false));
	}

	private static Stream<Arguments> parametersGetByIdAndStartInstant() {
		return Stream.of(
				Arguments.of(ImmutableJobInstanceIdentifier.builder()
						.startTime(Instant.parse("2022-01-01T06:00:00.000Z"))
						.jobId("3")
						.build(), true),
				Arguments.of(ImmutableJobInstanceIdentifier.builder()
						.startTime(Instant.parse("2022-01-01T06:00:00.000Z"))
						.jobId("1")
						.build(), false));
	}

	private static Stream<Arguments> parametersGetById() {
		return Stream.of(Arguments.of("5", true), Arguments.of("10000", false));
	}

	private static Stream<Arguments> parametersIsJobUnique() {
		return Stream.of(Arguments.of("5", true), Arguments.of("1", false));
	}

	// TEST SET-UP

	@BeforeAll
	static void setUpAll() {
		TimeUtils.useMockTime(MOCK_NOW, ZoneId.of("UTC"));
		AbstractAgent.disableGui();
	}

	@BeforeEach
	void init() {
		MOCK_JOBS = setUpServerJobs();
		setUpServerMock();
	}

	// TESTS

	@Test
	@DisplayName("Test getting available capacity for ALL jobs")
	void testGettingAvailableCapacityForAllJobs() {
		final Instant startTime = Instant.parse("2022-01-01T09:00:00.000Z");
		final Instant endTime = Instant.parse("2022-01-01T12:30:00.000Z");
		final int availableCapacity = serverAgent.manage().getAvailableCapacity(startTime, endTime, null, null);

		assertThat(availableCapacity).isEqualTo(153);
	}

	@Test
	@DisplayName("Test getting available capacity for ALL jobs with excluded job")
	void testGettingAvailableCapacityForAllJobsWithOneExcluded() {
		final JobInstanceIdentifier jobToExclude = ImmutableJobInstanceIdentifier.builder()
				.jobId("5")
				.startTime(Instant.parse("2022-01-01T11:00:00.000Z"))
				.build();
		final Instant startTime = Instant.parse("2022-01-01T09:00:00.000Z");
		final Instant endTime = Instant.parse("2022-01-01T12:30:00.000Z");
		final int availableCapacity = serverAgent.manage().getAvailableCapacity(startTime, endTime, jobToExclude, null);

		assertThat(availableCapacity).isEqualTo(171);
	}

	@Test
	@DisplayName("Test getting available capacity for BACK UP jobs")
	void testGettingAvailableCapacityForBackUpJobs() {
		final Instant startTime = Instant.parse("2022-01-01T09:00:00.000Z");
		final Instant endTime = Instant.parse("2022-01-01T12:30:00.000Z");
		final int availableCapacity = serverAgent.manage()
				.getAvailableCapacity(startTime, endTime, null, ServerPowerSourceType.BACK_UP_POWER);

		assertThat(availableCapacity).isEqualTo(188);
	}

	@Test
	@DisplayName("Test getting available capacity for GREEN ENERGY jobs")
	void testGettingAvailableCapacityForGreenEnergyJobs() {
		final Instant startTime = Instant.parse("2022-01-01T09:00:00.000Z");
		final Instant endTime = Instant.parse("2022-01-01T12:30:00.000Z");
		final int availableCapacity = serverAgent.manage()
				.getAvailableCapacity(startTime, endTime, null, ServerPowerSourceType.GREEN_ENERGY);

		assertThat(availableCapacity).isEqualTo(173);
	}

	@Test
	@DisplayName("Test getting available capacity for ALL jobs with PROCESSING")
	void testGettingAvailableCapacityForAllJobsIncludingProcessing() {
		final Job jobProcessing = ImmutableJob.builder()
				.jobId("1000")
				.clientIdentifier("Client1000")
				.startTime(Instant.parse("2022-01-01T05:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T13:30:00.000Z"))
				.power(10)
				.build();
		serverAgent.getServerJobs().put(jobProcessing, JobStatusEnum.PROCESSING);
		final Instant startTime = Instant.parse("2022-01-01T09:00:00.000Z");
		final Instant endTime = Instant.parse("2022-01-01T12:30:00.000Z");
		final int availableCapacity = serverAgent.manage().getAvailableCapacity(startTime, endTime, null, null);

		assertThat(availableCapacity).isEqualTo(143);
	}

	@Test
	@DisplayName("Test calculating price for job")
	void testPriceCalculation() {
		final GreenSourceData mockGreenSourceData = ImmutableGreenSourceData.builder()
				.jobId("1")
				.availablePowerInTime(100)
				.pricePerPowerUnit(5)
				.build();
		final double resultPrice = serverAgent.manage().calculateServicePrice(mockGreenSourceData);

		assertThat(resultPrice).isEqualTo(75);
	}

	@Test
	@DisplayName("Test getting current job instance not found")
	void testGettingCurrentJobInstanceNotFound() {
		final Map.Entry<Job, JobStatusEnum> result = serverAgent.manage().getCurrentJobInstance("1");
		assertNull(result);
	}

	@Test
	@DisplayName("Test getting current job instance one instance")
	void testGettingCurrentJobInstanceOneInstance() {
		final Map.Entry<Job, JobStatusEnum> result = serverAgent.manage().getCurrentJobInstance("2");

		assertNotNull(result);
		assertThat(result.getKey().getClientIdentifier()).isEqualTo("Client2");
		assertThat(result.getKey().getEndTime()).isEqualTo(Instant.parse("2022-01-01T11:00:00.000Z"));
	}

	@Test
	@DisplayName("Test getting current job instance two instances")
	void testGettingCurrentJobInstanceTwoInstances() {
		final Job jobProcessing = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Client1")
				.startTime(Instant.parse("2022-01-01T10:30:00.000Z"))
				.endTime(Instant.parse("2022-01-01T13:30:00.000Z"))
				.power(10)
				.build();
		serverAgent.getServerJobs().put(jobProcessing, JobStatusEnum.IN_PROGRESS);
		final Map.Entry<Job, JobStatusEnum> result = serverAgent.manage().getCurrentJobInstance("1");

		assertNotNull(result);
		assertThat(result.getKey().getPower()).isEqualTo(10);
		assertThat(result.getKey().getStartTime()).isEqualTo(Instant.parse("2022-01-01T10:30:00.000Z"));
	}

	@ParameterizedTest
	@MethodSource("parametersGetByIdAndStart")
	@DisplayName("Test getting job by id and start time")
	void testGettingJobByIdAndStartTime(final Instant startTime, final String jobId, final boolean result) {
		final Job jobResult = serverAgent.manage().getJobByIdAndStartDate(jobId, startTime);
		assertThat(Objects.nonNull(jobResult)).isEqualTo(result);
	}

	@ParameterizedTest
	@MethodSource("parametersGetByIdAndStartInstant")
	@DisplayName("Test getting job by id and start time instant")
	void testGettingJobByIdAndStartTimeInstant(final JobInstanceIdentifier jobInstance, final boolean result) {
		final Job jobResult = serverAgent.manage().getJobByIdAndStartDate(jobInstance);
		assertThat(Objects.nonNull(jobResult)).isEqualTo(result);
	}

	@ParameterizedTest
	@MethodSource("parametersGetById")
	@DisplayName("Test getting job by id")
	void testGettingJobById(final String jobId, final boolean result) {
		final Job jobResult = serverAgent.manage().getJobById(jobId);
		assertThat(Objects.nonNull(jobResult)).isEqualTo(result);
	}

	@ParameterizedTest
	@MethodSource("parametersIsJobUnique")
	@DisplayName("Test is job unique by id")
	void testIsJobUnique(final String jobId, final boolean result) {
		final Job jobProcessing = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Client1")
				.startTime(Instant.parse("2022-01-01T10:30:00.000Z"))
				.endTime(Instant.parse("2022-01-01T13:30:00.000Z"))
				.power(10)
				.build();
		serverAgent.getServerJobs().put(jobProcessing, JobStatusEnum.IN_PROGRESS);
		assertThat(serverAgent.manage().isJobUnique(jobId)).isEqualTo(result);
	}

	@Test
	@DisplayName("Test increment started unique job")
	void testIncrementStartedUniqueJob() {
		final String jobId = "1";

		serverAgent.manage().incrementStartedJobs(jobId);
		assertThat(MOCK_MANAGEMENT.getUniqueStartedJobs().get()).isEqualTo(1);
		assertThat(MOCK_MANAGEMENT.getStartedJobsInstances().get()).isEqualTo(1);
	}

	@Test
	@DisplayName("Test increment started non unique job")
	void testIncrementStartedNonUniqueJob() {
		final Job jobProcessing = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Client1")
				.startTime(Instant.parse("2022-01-01T10:30:00.000Z"))
				.endTime(Instant.parse("2022-01-01T13:30:00.000Z"))
				.power(10)
				.build();
		serverAgent.getServerJobs().put(jobProcessing, JobStatusEnum.IN_PROGRESS);
		final String jobId = "1";

		serverAgent.manage().incrementStartedJobs(jobId);
		assertThat(MOCK_MANAGEMENT.getUniqueStartedJobs().get()).isZero();
		assertThat(MOCK_MANAGEMENT.getStartedJobsInstances().get()).isEqualTo(1);
	}

	@Test
	@DisplayName("Test increment finished unique job")
	void testIncrementFinishedUniqueJob() {
		final String jobId = "1";

		serverAgent.manage().incrementFinishedJobs(jobId);
		assertThat(MOCK_MANAGEMENT.getUniqueFinishedJobs().get()).isEqualTo(1);
		assertThat(MOCK_MANAGEMENT.getFinishedJobsInstances().get()).isEqualTo(1);
	}

	@Test
	@DisplayName("Test increment finished non unique job")
	void testIncrementFinishedNonUniqueJob() {
		final Job jobProcessing = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Client1")
				.startTime(Instant.parse("2022-01-01T10:30:00.000Z"))
				.endTime(Instant.parse("2022-01-01T13:30:00.000Z"))
				.power(10)
				.build();
		serverAgent.getServerJobs().put(jobProcessing, JobStatusEnum.IN_PROGRESS);
		final String jobId = "1";

		serverAgent.manage().incrementFinishedJobs(jobId);
		assertThat(MOCK_MANAGEMENT.getUniqueFinishedJobs().get()).isZero();
		assertThat(MOCK_MANAGEMENT.getFinishedJobsInstances().get()).isEqualTo(1);
	}

	@Test
	@DisplayName("Test update maximum capacity")
	void testUpdateMaximumCapacity() {
		final int newCapacity = 50;

		serverAgent.manage().updateMaximumCapacity(newCapacity);
		assertThat(serverAgent.getCurrentMaximumCapacity()).isEqualTo(newCapacity);
	}

	@Test
	@DisplayName("Test job division - job after shortage start")
	void testJobDivisionAfterShortageStart() {
		final Instant startTime = Instant.parse("2022-01-01T09:00:00.000Z");
		final Job job = MOCK_JOBS.keySet().stream().filter(jobKey -> jobKey.getJobId().equals("5")).findFirst()
				.orElse(null);

		serverAgent.manage().divideJobForPowerShortage(Objects.requireNonNull(job), startTime);
		final JobStatusEnum statusAfterUpdate = serverAgent.getServerJobs().entrySet().stream()
				.filter(jobEntry -> jobEntry.getKey().equals(job))
				.map(Map.Entry::getValue)
				.findFirst().orElse(null);

		assertThat(serverAgent.getServerJobs()).hasSameSizeAs(MOCK_JOBS);
		assertTrue(serverAgent.getServerJobs().containsKey(job));
		assertThat(statusAfterUpdate).isEqualTo(ON_HOLD_TRANSFER);
	}

	@Test
	@DisplayName("Test job division - job during shortage start")
	void testJobDivisionDuringShortageStart() {
		final Instant startTime = Instant.parse("2022-01-01T09:00:00.000Z");
		final Job job = MOCK_JOBS.keySet().stream().filter(jobKey -> jobKey.getJobId().equals("2")).findFirst()
				.orElse(null);

		serverAgent.manage().divideJobForPowerShortage(Objects.requireNonNull(job), startTime);

		final Map<Job, JobStatusEnum> updatedJobInstances = serverAgent.getServerJobs().entrySet().stream()
				.filter(jobEntry -> jobEntry.getKey().getJobId().equals(job.getJobId()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		final Map.Entry<Job, JobStatusEnum> jobOnHold = updatedJobInstances.entrySet().stream()
				.filter(jobEntry -> jobEntry.getValue().equals(ON_HOLD_TRANSFER))
				.findFirst().orElse(null);
		final Map.Entry<Job, JobStatusEnum> jobInProgress = updatedJobInstances.entrySet().stream()
				.filter(jobEntry -> !jobEntry.getValue().equals(ON_HOLD_TRANSFER))
				.findFirst().orElse(null);

		assertThat(serverAgent.getServerJobs()).hasSize(7);
		assertFalse(serverAgent.getServerJobs().containsKey(job));
		assertThat(updatedJobInstances).hasSize(2);
		assertNotNull(jobOnHold);
		assertNotNull(jobInProgress);
		assertThat(jobOnHold.getKey().getStartTime()).isEqualTo(startTime);
		assertThat(jobInProgress.getKey().getEndTime()).isEqualTo(startTime);
	}

	@Test
	@DisplayName("Test finishing job not on back up power")
	void testFinishingJobNotBackUpPower() {
		final AID greenSourceForJob = mock(AID.class);
		final Job job = MOCK_JOBS.keySet().stream()
				.filter(jobKey -> jobKey.getJobId().equals("1")).findFirst()
				.orElse(null);

		serverAgent.getGreenSourceForJobMap().put(Objects.requireNonNull(job).getJobId(), greenSourceForJob);
		assertThat(serverAgent.getGreenSourceForJobMap()).hasSize(1);

		serverAgent.manage().finishJobExecution(job, false);

		assertThat(serverAgent.getServerJobs()).hasSize(5);
		assertThat(serverAgent.getGreenSourceForJobMap()).isEmpty();
		assertThat(MOCK_MANAGEMENT.getUniqueFinishedJobs().get()).isEqualTo(1);
		assertThat(MOCK_MANAGEMENT.getFinishedJobsInstances().get()).isEqualTo(1);
	}

	@Test
	@DisplayName("Test finishing job on back up power")
	void testFinishingJobBackUpPower() {
		final Job job = MOCK_JOBS.keySet().stream().filter(jobKey -> jobKey.getJobId().equals("2")).findFirst()
				.orElse(null);

		serverAgent.manage().finishJobExecution(job, false);

		final JobStatusEnum updatedStatus = serverAgent.getServerJobs().entrySet().stream()
				.filter(jobEntry -> jobEntry.getKey().getJobId().equals("3"))
				.map(Map.Entry::getValue)
				.findFirst().orElse(null);

		assertThat(serverAgent.getServerJobs()).hasSize(5);
		assertThat(MOCK_MANAGEMENT.getUniqueFinishedJobs().get()).isEqualTo(1);
		assertThat(MOCK_MANAGEMENT.getFinishedJobsInstances().get()).isEqualTo(1);
		assertNotNull(updatedStatus);
		assertThat(updatedStatus).isEqualTo(IN_PROGRESS_BACKUP_ENERGY);
	}

	/**
	 * Class creates mock server jobs used in test scenarios.
	 * The following structure was used:
	 *
	 * Job1 -> power: 10, time: 08:00 - 10:30, status: IN_PROGRESS,
	 * Job2 -> power: 12, time: 07:30 - 11:00, status: IN_PROGRESS_BACKUP
	 * Job3 -> power: 5,  time: 06:00 - 15:00, status: ON_HOLD_SOURCE_SHORTAGE
	 * Job4 -> power: 2,  time: 09:00 - 12:00, status: IN_PROGRESS
	 * Job5 -> power: 25, time: 11:00 - 12:00, status: ACCEPTED
	 * Job6 -> power: 15, time: 11:30 - 13:00, status: ON_HOLD_TRANSFER
	 */
	private Map<Job, JobStatusEnum> setUpServerJobs() {
		final Job mockJob1 = ImmutableJob.builder()
				.jobId("1")
				.clientIdentifier("Client1")
				.startTime(Instant.parse("2022-01-01T08:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T10:30:00.000Z"))
				.power(10)
				.build();
		final Job mockJob2 = ImmutableJob.builder()
				.jobId("2")
				.clientIdentifier("Client2")
				.startTime(Instant.parse("2022-01-01T07:30:00.000Z"))
				.endTime(Instant.parse("2022-01-01T11:00:00.000Z"))
				.power(12)
				.build();
		final Job mockJob3 = ImmutableJob.builder()
				.jobId("3")
				.clientIdentifier("Client3")
				.startTime(Instant.parse("2022-01-01T06:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T15:00:00.000Z"))
				.power(5)
				.build();
		final Job mockJob4 = ImmutableJob.builder()
				.jobId("4")
				.clientIdentifier("Client4")
				.startTime(Instant.parse("2022-01-01T09:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T12:00:00.000Z"))
				.power(2)
				.build();
		final Job mockJob5 = ImmutableJob.builder()
				.jobId("5")
				.clientIdentifier("Client5")
				.startTime(Instant.parse("2022-01-01T11:00:00.000Z"))
				.endTime(Instant.parse("2022-01-01T12:00:00.000Z"))
				.power(25)
				.build();
		final Job mockJob6 = ImmutableJob.builder()
				.jobId("6")
				.clientIdentifier("Client6")
				.startTime(Instant.parse("2022-01-01T11:30:00.000Z"))
				.endTime(Instant.parse("2022-01-01T13:00:00.000Z"))
				.power(15)
				.build();
		final Map<Job, JobStatusEnum> mockJobMap = new HashMap<>();
		mockJobMap.put(mockJob1, JobStatusEnum.IN_PROGRESS);
		mockJobMap.put(mockJob2, JobStatusEnum.IN_PROGRESS_BACKUP_ENERGY);
		mockJobMap.put(mockJob3, JobStatusEnum.ON_HOLD_SOURCE_SHORTAGE);
		mockJobMap.put(mockJob4, JobStatusEnum.IN_PROGRESS);
		mockJobMap.put(mockJob5, JobStatusEnum.ACCEPTED);
		mockJobMap.put(mockJob6, ON_HOLD_TRANSFER);
		return mockJobMap;
	}

	private void setUpServerMock() {
		serverAgent = spy(ServerAgent.class);
		serverAgent.getServerJobs().putAll(MOCK_JOBS);
		serverAgent.setCurrentMaximumCapacity(MOCK_CAPACITY);

		final ServerStateManagement management = new ServerStateManagement(serverAgent);
		MOCK_MANAGEMENT = spy(management);

		doReturn(MOCK_PRICE).when(serverAgent).getPricePerHour();
		doReturn(MOCK_CAPACITY).when(serverAgent).getInitialMaximumCapacity();
		doReturn(MOCK_MANAGEMENT).when(serverAgent).manage();
		doNothing().when(serverAgent).addBehaviour(any());
		doNothing().when(serverAgent).send(any());
	}
}