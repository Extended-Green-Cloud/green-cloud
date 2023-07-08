package com.gui.agents.server;

import static com.database.knowledge.domain.agent.DataType.SERVER_MONITORING;
import static org.greencloud.commons.args.agent.AgentType.SERVER;
import static org.greencloud.commons.enums.job.JobExecutionResultEnum.ACCEPTED;
import static org.greencloud.commons.enums.job.JobExecutionResultEnum.FAILED;
import static org.greencloud.commons.enums.job.JobExecutionStatusEnum.ACCEPTED_JOB_STATUSES;
import static org.greencloud.commons.enums.job.JobExecutionStatusEnum.IN_PROGRESS_BACKUP_ENERGY;
import static org.greencloud.commons.enums.job.JobExecutionStatusEnum.JOB_ON_HOLD_STATUSES;
import static org.greencloud.commons.utils.job.JobUtils.getJobCount;
import static org.greencloud.commons.utils.job.JobUtils.getJobSuccessRatio;
import static com.gui.websocket.WebSocketConnections.getAgentsWebSocket;
import static com.gui.websocket.WebSocketConnections.getCloudNetworkSocket;
import static java.util.Collections.singleton;

import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import com.database.knowledge.domain.agent.server.ImmutableServerMonitoringData;
import com.database.knowledge.domain.agent.server.ServerMonitoringData;
import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.args.agent.server.node.ServerNodeArgs;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.enums.job.JobExecutionStatusEnum;
import org.greencloud.commons.domain.resources.HardwareResources;

import com.gui.agents.AbstractNetworkNode;
import com.gui.event.AbstractEvent;
import com.gui.message.ImmutableDisableServerMessage;
import com.gui.message.ImmutableEnableServerMessage;
import com.gui.message.ImmutableSetNumericValueMessage;
import com.gui.message.ImmutableUpdateResourcesMessage;
import com.gui.message.ImmutableUpdateSingleValueMessage;

import jade.util.leap.Serializable;

/**
 * Agent node class representing the server
 */
public class ServerNode extends AbstractNetworkNode<ServerNodeArgs, ServerAgentProps> implements Serializable {

	public ServerNode() {
		super();
	}

	/**
	 * Server node constructor
	 *
	 * @param serverNodeArgs aarguments of given server node
	 */
	public ServerNode(ServerNodeArgs serverNodeArgs) {
		super(serverNodeArgs, SERVER);
	}

	/**
	 * Function updates the current back-up traffic to given value
	 *
	 * @param backUpPowerInUse current power in use coming from back-up energy
	 */
	public void updateBackUpTraffic(final double backUpPowerInUse) {
		getAgentsWebSocket().send(ImmutableSetNumericValueMessage.builder()
				.data(backUpPowerInUse)
				.agentName(agentName)
				.type("SET_SERVER_BACK_UP_TRAFFIC")
				.build());
	}

	/**
	 * Function updates current in-use resources
	 *
	 * @param resources              currently utilized resources
	 * @param powerConsumption       current power consumption
	 * @param powerConsumptionBackUp current back-up power consumption
	 */
	public void updateResources(final HardwareResources resources, final double powerConsumption,
			final double powerConsumptionBackUp) {
		getAgentsWebSocket().send(ImmutableUpdateResourcesMessage.builder()
				.resources(resources)
				.powerConsumption(powerConsumption)
				.powerConsumptionBackUp(powerConsumptionBackUp)
				.agentName(agentName)
				.build());
	}

	/**
	 * Function updates the number of clients
	 *
	 * @param value new clients count
	 */
	public void updateClientNumber(final int value) {
		getAgentsWebSocket().send(ImmutableSetNumericValueMessage.builder()
				.data(value)
				.agentName(agentName)
				.type("SET_CLIENT_NUMBER")
				.build());
	}

	/**
	 * Function disables the server
	 */
	public void disableServer() {
		getAgentsWebSocket().send(ImmutableDisableServerMessage.builder()
				.cna(nodeArgs.getCloudNetworkAgent())
				.server(agentName)
				.cpu(nodeArgs.getCpu())
				.build());
	}

	/**
	 * Function enables the server
	 */
	public void enableServer() {
		getAgentsWebSocket().send(ImmutableEnableServerMessage.builder()
				.cna(nodeArgs.getCloudNetworkAgent())
				.cna(nodeArgs.getCloudNetworkAgent())
				.server(agentName)
				.cpu(nodeArgs.getCpu())
				.build());
	}

	/**
	 * Function announce new accepted job in the network
	 */
	public void announceClientJob() {
		getCloudNetworkSocket().send(ImmutableUpdateSingleValueMessage.builder()
				.data(1)
				.type("UPDATE_CURRENT_PLANNED_JOBS")
				.build());
	}

	public Optional<AbstractEvent> getEvent() {
		return Optional.ofNullable(eventsQueue.poll());
	}

	@Override
	public void updateGUI(final ServerAgentProps props) {
		final double successRatio = getJobSuccessRatio(props.getJobCounters().get(ACCEPTED).getCount(),
				props.getJobCounters().get(FAILED).getCount());
		final double backUpTraffic = props.getCPUUsage(singleton(IN_PROGRESS_BACKUP_ENERGY));
		final HardwareResources inUseResources = props.getInUseResources();
		final double powerConsumption = props.getCurrentPowerConsumption();
		final double powerConsumptionBackUp = props.getCurrentPowerConsumptionBackUp();
		final ConcurrentMap<ClientJob, JobExecutionStatusEnum> jobs = props.getServerJobs();

		updateTraffic(props.getCPUUsage(null));
		updateBackUpTraffic(backUpTraffic);
		updateResources(inUseResources, powerConsumption, powerConsumptionBackUp);
		updateJobsCount(getJobCount(jobs));
		updateJobsOnHoldCount(getJobCount(jobs, JOB_ON_HOLD_STATUSES));
		updateClientNumber(getJobCount(jobs, ACCEPTED_JOB_STATUSES));
		updateIsActive(getIsActiveState(props));
		updateCurrentJobSuccessRatio(successRatio);
		saveMonitoringData(props);
	}

	/**
	 * Method saves monitoring data of given agent node to database
	 *
	 * @param props current properties of Server agent
	 */
	@Override
	public void saveMonitoringData(final ServerAgentProps props) {
		final double greenPowerUsage = props.getCPUUsage(null);
		final double backPowerUsage = props.getCPUUsage(singleton(IN_PROGRESS_BACKUP_ENERGY));
		final int jobsNo = props.getServerJobs().size() - getJobCount(props.getServerJobs(), JOB_ON_HOLD_STATUSES);
		final double successRatio = getJobSuccessRatio(props.getJobCounters().get(ACCEPTED).getCount(),
				props.getJobCounters().get(FAILED).getCount());

		final ServerMonitoringData serverMonitoringData = ImmutableServerMonitoringData.builder()
				.idlePowerConsumption(props.getIdlePowerConsumption())
				.currentPowerConsumption(props.getCurrentPowerConsumption())
				.currentTraffic(greenPowerUsage)
				.currentBackUpPowerTraffic(backPowerUsage)
				.serverJobs(jobsNo)
				.successRatio(successRatio)
				.isDisabled(props.isDisabled())
				.build();

		writeMonitoringData(SERVER_MONITORING, serverMonitoringData, props.getAgentName());
	}

	private boolean getIsActiveState(final ServerAgentProps props) {
		return props.getCPUUsage(null) > 0 || props.getCPUUsage(singleton(IN_PROGRESS_BACKUP_ENERGY)) > 0;
	}
}