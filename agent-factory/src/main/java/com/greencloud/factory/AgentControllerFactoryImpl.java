package com.greencloud.factory;

import static com.greencloud.factory.constants.AgentControllerConstants.GRAPH_INITIALIZATION_DELAY;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.database.knowledge.timescale.TimescaleDatabase;
import com.greencloud.commons.args.agent.AgentArgs;
import com.greencloud.commons.args.agent.client.ClientAgentArgs;
import com.greencloud.commons.args.agent.cloudnetwork.CloudNetworkArgs;
import com.greencloud.commons.args.agent.greenenergy.GreenEnergyAgentArgs;
import com.greencloud.commons.args.agent.monitoring.MonitoringAgentArgs;
import com.greencloud.commons.args.agent.scheduler.SchedulerAgentArgs;
import com.greencloud.commons.args.agent.server.ServerAgentArgs;
import com.greencloud.commons.exception.JadeControllerException;
import com.greencloud.commons.scenario.ScenarioStructureArgs;
import com.gui.agents.AbstractAgentNode;
import com.gui.controller.GuiController;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class AgentControllerFactoryImpl implements AgentControllerFactory {

	private static final Logger logger = LoggerFactory.getLogger(AgentControllerFactoryImpl.class);

	private final AgentNodeFactory agentNodeFactory;
	private final ContainerController containerController;
	private final TimescaleDatabase timescaleDatabase;
	private final GuiController guiController;
	private final String mainDFAddress;
	private final String mainHostPlatformId;

	public AgentControllerFactoryImpl(final ContainerController containerController,
			final TimescaleDatabase timescaleDatabase,
			final GuiController guiController) {
		this.agentNodeFactory = new AgentNodeFactoryImpl();
		this.containerController = containerController;
		this.timescaleDatabase = timescaleDatabase;
		this.guiController = guiController;
		this.mainDFAddress = null;
		this.mainHostPlatformId = null;
	}

	public AgentControllerFactoryImpl(final ContainerController containerController,
			final TimescaleDatabase timescaleDatabase,
			final GuiController guiController,
			final String mainDFAddress,
			final String mainHostPlatformId) {
		this.agentNodeFactory = new AgentNodeFactoryImpl();
		this.containerController = containerController;
		this.timescaleDatabase = timescaleDatabase;
		this.guiController = guiController;
		this.mainDFAddress = mainDFAddress;
		this.mainHostPlatformId = mainHostPlatformId;
	}

	@Override
	public AgentController createAgentController(final AgentArgs agentArgs, final ScenarioStructureArgs scenario) {
		final AbstractAgentNode agentNode = agentNodeFactory.createAgentNode(agentArgs, scenario);
		agentNode.setDatabaseClient(timescaleDatabase);
		guiController.addAgentNodeToGraph(agentNode);

		try {
			logger.info("Created {} agent.", agentArgs.getName());
			if (agentArgs instanceof ClientAgentArgs clientAgent) {
				return createClientController(clientAgent, guiController, agentNode);
			} else if (agentArgs instanceof ServerAgentArgs serverAgent) {
				return createServerController(serverAgent, guiController, agentNode);
			} else if (agentArgs instanceof CloudNetworkArgs cloudNetworkAgent) {
				return createCloudNetworkController(cloudNetworkAgent, guiController, agentNode);
			} else if (agentArgs instanceof GreenEnergyAgentArgs greenEnergyAgent) {
				return createGreenSourceController(greenEnergyAgent, guiController, agentNode);
			} else if (agentArgs instanceof MonitoringAgentArgs monitoringAgent) {
				return createMonitoringController(monitoringAgent, guiController, agentNode);
			} else if (agentArgs instanceof SchedulerAgentArgs schedulerAgent) {
				return createSchedulerController(schedulerAgent, guiController, agentNode);
			}
			return null;
		} catch (StaleProxyException e) {
			throw new JadeControllerException("Failed to run agent controller", e);
		}
	}

	@Override
	public void runAgentControllers(final List<AgentController> controllers, final long agentRunDelay) {
		final ScheduledExecutorService scheduledExecutor = newSingleThreadScheduledExecutor();
		scheduledExecutor.schedule(
				() -> controllers.forEach(controller -> runAgentController(controller, agentRunDelay)),
				GRAPH_INITIALIZATION_DELAY, SECONDS);
		shutdownAndAwaitTermination(scheduledExecutor);
	}

	@Override
	public void runAgentController(final AgentController controller, long pause) {
		try {
			controller.start();
			controller.activate();
			TimeUnit.MILLISECONDS.sleep(pause);
		} catch (StaleProxyException | InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new JadeControllerException("Failed to run agent controller", e);
		}
	}

	@Override
	public void shutdownAndAwaitTermination(final ExecutorService executorService) {
		executorService.shutdown();
		try {
			if (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
				executorService.shutdownNow();
			}
		} catch (InterruptedException ie) {
			executorService.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

	private AgentController createClientController(final ClientAgentArgs clientAgent, final GuiController guiController,
			final AbstractAgentNode agentNode) throws StaleProxyException {
		final String startDate = clientAgent.formatClientTime(clientAgent.getStart());
		final String endDate = clientAgent.formatClientTime(clientAgent.getEnd());
		final String deadline = clientAgent.formatClientTime(clientAgent.getDeadline());

		return containerController.createNewAgent(clientAgent.getName(),
				"com.greencloud.application.agents.client.ClientAgent",
				new Object[] { agentNode,
						guiController,
						mainDFAddress,
						mainHostPlatformId,
						startDate,
						endDate,
						deadline,
						clientAgent.getPower(),
						clientAgent.getJobId() });
	}

	private AgentController createSchedulerController(final SchedulerAgentArgs schedulerAgent,
			final GuiController guiController, final AbstractAgentNode agentNode) throws StaleProxyException {
		return containerController.createNewAgent(schedulerAgent.getName(),
				"com.greencloud.application.agents.scheduler.SchedulerAgent",
				new Object[] { agentNode,
						guiController,
						schedulerAgent.getDeadlineWeight(),
						schedulerAgent.getPowerWeight(),
						schedulerAgent.getMaximumQueueSize(),
						schedulerAgent.getJobSplitThreshold(),
						schedulerAgent.getSplittingFactor() });
	}

	private AgentController createCloudNetworkController(final CloudNetworkArgs cloudNetworkAgent,
			final GuiController guiController, final AbstractAgentNode agentNode) throws StaleProxyException {
		return containerController.createNewAgent(cloudNetworkAgent.getName(),
				"com.greencloud.application.agents.cloudnetwork.CloudNetworkAgent",
				new Object[] { agentNode,
						guiController,
						mainDFAddress,
						mainHostPlatformId });
	}

	private AgentController createServerController(final ServerAgentArgs serverAgent, final GuiController guiController,
			final AbstractAgentNode agentNode) throws StaleProxyException {
		return containerController.createNewAgent(serverAgent.getName(),
				"com.greencloud.application.agents.server.ServerAgent",
				new Object[] { agentNode,
						guiController,
						serverAgent.getOwnerCloudNetwork(),
						serverAgent.getPrice(),
						serverAgent.getMaximumCapacity(),
						serverAgent.getJobProcessingLimit() });
	}

	private AgentController createGreenSourceController(final GreenEnergyAgentArgs greenEnergyAgent,
			final GuiController guiController, final AbstractAgentNode agentNode) throws StaleProxyException {
		return containerController.createNewAgent(greenEnergyAgent.getName(),
				"com.greencloud.application.agents.greenenergy.GreenEnergyAgent",
				new Object[] { agentNode,
						guiController,
						greenEnergyAgent.getMonitoringAgent(),
						greenEnergyAgent.getOwnerSever(),
						greenEnergyAgent.getMaximumCapacity(),
						greenEnergyAgent.getPricePerPowerUnit(),
						greenEnergyAgent.getLatitude(),
						greenEnergyAgent.getLongitude(),
						greenEnergyAgent.getEnergyType(),
						greenEnergyAgent.getWeatherPredictionError() });
	}

	private AgentController createMonitoringController(final MonitoringAgentArgs monitoringAgent,
			final GuiController guiController, final AbstractAgentNode agentNode) throws StaleProxyException {
		return containerController.createNewAgent(monitoringAgent.getName(),
				"com.greencloud.application.agents.monitoring.MonitoringAgent",
				new Object[] { agentNode,
						guiController,
						monitoringAgent.getBadStubProbability() });
	}
}