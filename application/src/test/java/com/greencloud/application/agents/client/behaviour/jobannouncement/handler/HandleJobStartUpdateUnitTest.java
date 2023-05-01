package com.greencloud.application.agents.client.behaviour.jobannouncement.handler;

import static com.greencloud.application.agents.client.behaviour.jobannouncement.domain.Fixtures.buildJobStatusUpdate;
import static com.greencloud.application.agents.client.behaviour.jobannouncement.domain.Fixtures.setUpClient;
import static com.greencloud.application.agents.client.behaviour.jobannouncement.domain.Fixtures.setUpClientMultipleJobParts;
import static com.greencloud.application.agents.client.behaviour.jobannouncement.handler.logs.JobAnnouncementHandlerLog.CLIENT_JOB_FINISH_DELAY_BEFORE_DEADLINE_DELAY_LOG;
import static com.greencloud.application.agents.client.behaviour.jobannouncement.handler.logs.JobAnnouncementHandlerLog.CLIENT_JOB_START_DELAY_LOG;
import static com.greencloud.application.agents.client.behaviour.jobannouncement.handler.logs.JobAnnouncementHandlerLog.CLIENT_JOB_START_ON_TIME_LOG;
import static com.greencloud.application.agents.client.domain.enums.ClientJobUpdateEnum.STARTED_JOB_ID;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.CREATED;
import static com.greencloud.commons.domain.job.enums.JobClientStatusEnum.IN_PROGRESS;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.time.Instant.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.quality.Strictness.LENIENT;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import com.greencloud.application.agents.client.ClientAgent;
import com.greencloud.application.agents.client.management.ClientManagement;
import com.greencloud.application.domain.job.JobStatusUpdate;
import com.greencloud.commons.message.MessageBuilder;
import com.gui.agents.ClientAgentNode;
import com.gui.controller.GuiController;

import nl.altindag.log.LogCaptor;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class HandleJobStartUpdateUnitTest {

	private static final JobStatusUpdate JOB_STATUS = buildJobStatusUpdate("1#part1");

	@Mock
	private static ClientAgent mockClientAgent;
	@Mock
	private static ClientAgentNode mockClientNode;
	@Mock
	private static ClientManagement mockClientManagement;
	@Mock
	private static GuiController mockGuiController;

	@Mock
	private static HandleJobStartUpdate testBehaviour;

	@BeforeEach
	void setUp() {
		var message = MessageBuilder.builder()
				.withPerformative(INFORM)
				.withObjectContent(JOB_STATUS)
				.build();

		mockClientAgent = setUpClient();
		mockClientNode = mock(ClientAgentNode.class);
		mockClientAgent.setAgentNode(mockClientNode);
		doReturn(mockGuiController).when(mockClientAgent).getGuiController();

		mockClientManagement = spy(new ClientManagement(mockClientAgent));
		doReturn(mockClientManagement).when(mockClientAgent).manage();

		testBehaviour = spy(new HandleJobStartUpdate(message, mockClientAgent, STARTED_JOB_ID));
		doNothing().when(mockClientAgent).writeMonitoringData(any(), any());
	}

	@Test
	@DisplayName("Test behaviour action for job with no parts")
	void testActionJobNotSplit() {
		// given
		doReturn(false).when(mockClientAgent).isSplit();

		// when
		testBehaviour.action();

		// then
		verify(testBehaviour).updateInformationOfJobStatusUpdate(JOB_STATUS);
		verify(testBehaviour, times(0)).updateInformationOfJobPartStatusUpdate(JOB_STATUS);
		verify(mockClientNode).updateJobStatus(IN_PROGRESS);

		assertThat(mockClientAgent.getJobExecution().getJobStatus()).isEqualTo(IN_PROGRESS);
	}

	@Test
	@DisplayName("Test behaviour action for job with parts not all started")
	void testActionJobPartsNotAllStarted() {
		// given
		setUpClientMultipleJobParts(mockClientAgent);
		doReturn(true).when(mockClientAgent).isSplit();

		// when
		testBehaviour.action();

		// then
		verify(testBehaviour).updateInformationOfJobPartStatusUpdate(JOB_STATUS);

		assertThat(mockClientManagement.checkIfAllPartsMatchStatus(IN_PROGRESS)).isFalse();
		assertThat(mockClientAgent.getJobParts()).containsKey("1#part1");
		assertThat(mockClientAgent.getJobParts().get("1#part1").getJobStatus()).isEqualTo(IN_PROGRESS);
		assertThat(mockClientAgent.getJobExecution().getJobStatus()).isEqualTo(CREATED);
	}

	@Test
	@DisplayName("Test behaviour action for job with all job parts started")
	void testActionJobPartsAllStarted() {
		// given
		doReturn(true).when(mockClientAgent).isSplit();

		// when
		testBehaviour.action();

		// then
		verify(testBehaviour).updateInformationOfJobPartStatusUpdate(JOB_STATUS);
		verify(mockClientNode).updateJobStatus(IN_PROGRESS);

		assertThat(mockClientManagement.checkIfAllPartsMatchStatus(IN_PROGRESS)).isTrue();
		assertThat(mockClientAgent.getJobParts()).containsKey("1#part1");
		assertThat(mockClientAgent.getJobParts().get("1#part1").getJobStatus()).isEqualTo(IN_PROGRESS);
		assertThat(mockClientAgent.getJobExecution().getJobStatus()).isEqualTo(IN_PROGRESS);
	}

	@Test
	@DisplayName("Test check job start time - start with delay")
	void testCheckIfJobStartedOnTimeWithDelay() {
		// given
		var jobStartTime = parse("2022-01-01T11:00:00.000Z");
		var actualStartTime = parse("2022-01-01T11:30:00.100Z");
		var expectedLog = CLIENT_JOB_START_DELAY_LOG.replace("{}", "-21601");

		// prepare
		var logCaptor = LogCaptor.forClass(HandleJobStartUpdate.class);
		logCaptor.setLogLevelToInfo();

		// when
		testBehaviour.checkIfJobStartedOnTime(jobStartTime, actualStartTime);

		// then
		assertThat(logCaptor.getInfoLogs()).containsExactly(expectedLog);
	}

	@Test
	@DisplayName("Test check job start time - start with no delay")
	void testCheckIfJobStartedOnTimeNoDelay() {
		// given
		var jobStartTime = parse("2022-01-01T11:00:00.000Z");
		var actualStartTime = parse("2022-01-01T11:00:00.100Z");

		// prepare
		var logCaptor = LogCaptor.forClass(HandleJobStartUpdate.class);
		logCaptor.setLogLevelToInfo();

		// when
		testBehaviour.checkIfJobStartedOnTime(jobStartTime, actualStartTime);

		// then
		assertThat(logCaptor.getInfoLogs()).containsExactly(CLIENT_JOB_START_ON_TIME_LOG);
	}
}