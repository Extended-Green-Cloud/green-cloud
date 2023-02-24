package org.greencloud.managingsystem.service.monitoring.goalservices;

import static com.database.knowledge.domain.agent.DataType.CLOUD_NETWORK_MONITORING;
import static com.database.knowledge.domain.agent.DataType.SERVER_MONITORING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import java.util.List;

import org.greencloud.managingsystem.agent.ManagingAgent;
import org.greencloud.managingsystem.service.monitoring.MonitoringService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.database.knowledge.domain.agent.AgentData;
import com.database.knowledge.domain.agent.cloudnetwork.CloudNetworkMonitoringData;
import com.database.knowledge.domain.agent.cloudnetwork.ImmutableCloudNetworkMonitoringData;
import com.database.knowledge.domain.agent.server.ImmutableServerMonitoringData;
import com.database.knowledge.domain.agent.server.ServerMonitoringData;
import com.database.knowledge.timescale.TimescaleDatabase;
import com.gui.agents.ManagingAgentNode;

class TrafficDistributionServiceTest {

	@Mock
	private ManagingAgent mockManagingAgent;

	@Mock
	private ManagingAgentNode mockAgentNode;

	@Mock
	private TimescaleDatabase mockDatabase;

	@Mock
	private MonitoringService mockMonitoringService;

	private TrafficDistributionService trafficDistributionService;

	@BeforeEach
	void setUp() {
		mockManagingAgent = mock(ManagingAgent.class);
		mockAgentNode = mock(ManagingAgentNode.class);
		mockDatabase = mock(TimescaleDatabase.class);
		mockMonitoringService = mock(MonitoringService.class);

		trafficDistributionService = new TrafficDistributionService(mockManagingAgent);

		doReturn(mockAgentNode).when(mockManagingAgent).getAgentNode();
		doReturn(mockDatabase).when(mockAgentNode).getDatabaseClient();
		doReturn(mockMonitoringService).when(mockManagingAgent).monitor();
	}

	@Test
	@DisplayName("Test compute coefficient")
	void testComputeCoefficient() {
		double coefficient = trafficDistributionService.computeCoefficient(List.of(1.0, 2.0, 3.0, 4.0, 5.0));

		assertThat(coefficient).isEqualTo(0.5270462766947299);
	}

	@Test
	@DisplayName("Test compute goal quality for CNA")
	void testComputeGoalQualityForCNA() {
		var cnaData = prepareCNAData();
		var cnaList = cnaData.stream().map(AgentData::aid).toList();
		double goalQuality = trafficDistributionService.computeGoalQualityForAgent(cnaData, cnaList);

		assertThat(goalQuality).isEqualTo(0.565685424949238);
	}

	@Test
	@DisplayName("Test compute goal quality for server")
	void testComputeGoalQualityForServer() {
		var serverData = prepareServerData();
		var serverList = serverData.stream().map(AgentData::aid).toList();
		double goalQuality = trafficDistributionService.computeGoalQualityForAgent(serverData, serverList);

		assertThat(goalQuality).isEqualTo(0.3666479606152469);
	}

	private List<AgentData> prepareCNAData() {
		final CloudNetworkMonitoringData data1 = ImmutableCloudNetworkMonitoringData.builder()
				.successRatio(0.5)
				.currentTraffic(0.5)
				.availablePower(50.0)
				.build();
		final CloudNetworkMonitoringData data2 = ImmutableCloudNetworkMonitoringData.builder()
				.successRatio(0.5)
				.availablePower(100.0)
				.currentTraffic(0.5)
				.build();
		final CloudNetworkMonitoringData data3 = ImmutableCloudNetworkMonitoringData.builder()
				.successRatio(0.5)
				.availablePower(150.0)
				.currentTraffic(0.5)
				.build();
		final CloudNetworkMonitoringData data4 = ImmutableCloudNetworkMonitoringData.builder()
				.successRatio(0.5)
				.availablePower(200.0)
				.currentTraffic(0.5)
				.build();
		return List.of(
				new AgentData(Instant.now(), "test_aid1", CLOUD_NETWORK_MONITORING, data1),
				new AgentData(Instant.now(), "test_aid1", CLOUD_NETWORK_MONITORING, data2),
				new AgentData(Instant.now(), "test_aid2", CLOUD_NETWORK_MONITORING, data3),
				new AgentData(Instant.now(), "test_aid2", CLOUD_NETWORK_MONITORING, data4)
		);
	}

	private List<AgentData> prepareServerData() {
		final ServerMonitoringData data1 = ImmutableServerMonitoringData.builder()
				.currentMaximumCapacity(150)
				.currentTraffic(1.0 / 3.0)
				.availablePower(100D)
				.currentBackUpPowerUsage(0.0)
				.successRatio(0.0)
				.isDisabled(false)
				.build();
		final ServerMonitoringData data2 = ImmutableServerMonitoringData.builder()
				.currentMaximumCapacity(200)
				.currentTraffic(0.25)
				.availablePower(150D)
				.currentBackUpPowerUsage(0.0)
				.successRatio(0.0)
				.isDisabled(false)
				.build();
		final ServerMonitoringData data3 = ImmutableServerMonitoringData.builder()
				.currentMaximumCapacity(250)
				.currentTraffic(0.2)
				.availablePower(200D)
				.currentBackUpPowerUsage(0.0)
				.successRatio(0.0)
				.isDisabled(false)
				.build();
		final ServerMonitoringData data4 = ImmutableServerMonitoringData.builder()
				.currentMaximumCapacity(300)
				.currentTraffic(0.25)
				.availablePower(225D)
				.currentBackUpPowerUsage(0.0)
				.successRatio(0.0)
				.isDisabled(false)
				.build();
		return List.of(
				new AgentData(Instant.now(), "test_aid1", SERVER_MONITORING, data1),
				new AgentData(Instant.now(), "test_aid1", SERVER_MONITORING, data2),
				new AgentData(Instant.now(), "test_aid2", SERVER_MONITORING, data3),
				new AgentData(Instant.now(), "test_aid2", SERVER_MONITORING, data4)
		);
	}
}
