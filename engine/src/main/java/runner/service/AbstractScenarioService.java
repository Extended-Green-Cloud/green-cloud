package runner.service;

import static jade.core.Runtime.instance;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static runner.service.domain.ScenarioConstants.CLIENTS_CONTAINER_ID;
import static runner.service.domain.ScenarioConstants.END_TIME_MAX;
import static runner.service.domain.ScenarioConstants.MAX_JOB_POWER;
import static runner.service.domain.ScenarioConstants.MIN_JOB_POWER;
import static runner.service.domain.ScenarioConstants.RESOURCE_SCENARIO_PATH;
import static runner.service.domain.ScenarioConstants.START_TIME_MAX;
import static runner.service.domain.ScenarioConstants.START_TIME_MIN;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.gui.controller.GUIController;
import com.gui.controller.GUIControllerImpl;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import runner.domain.AgentArgs;
import runner.domain.ClientAgentArgs;
import runner.domain.ImmutableClientAgentArgs;
import runner.domain.ScenarioArgs;
import runner.factory.AgentControllerFactory;
import runner.service.domain.exception.InvalidScenarioException;
import runner.service.domain.exception.JadeContainerException;
import runner.service.domain.exception.JadeControllerException;

/**
 * Abstract class serving as common base to Single and Multi Scenario Services.
 * It handles creation of Main and Agent's Containers as well as Agent's Controllers.
 * It is also responsible for running Agent's and Agent's clients.
 */
public abstract class AbstractScenarioService {

	private static final Logger logger = LoggerFactory.getLogger(AbstractScenarioService.class);

	private static final Long GRAPH_INITIALIZATION_PAUSE = 7L;
	private static final Integer RUN_CLIENT_AGENT_PAUSE = 150;
	private static final Integer RUN_AGENT_PAUSE = 100;

	protected static final XmlMapper xmlMapper = new XmlMapper();
	protected static final ExecutorService executorService = Executors.newCachedThreadPool();

	protected final GUIController guiController;
	protected final String fileName;
	protected final Runtime jadeRuntime;
	protected final ContainerController mainContainer;

	/**
	 * Constructor called by {@link MultiContainerScenarioService} and {@link SingleContainerScenarioService}
	 * Launches gui and the main controller. In case of MultiContainer case runs environment only for the main host.
	 *
	 * @param fileName name of the XML scenario document
	 */
	protected AbstractScenarioService(String fileName)
			throws ExecutionException, InterruptedException, StaleProxyException {
		this.guiController = new GUIControllerImpl();
		this.fileName = fileName;
		this.jadeRuntime = instance();

		executorService.execute(guiController);
		mainContainer = runMainController();
		runJadeGui();
	}

	/**
	 * Runs remote AgentContainer with GUI.
	 *
	 * @param fileName   name of the XML scenario document
	 * @param hostId     number of the host id
	 * @param mainHostIp IP address of the main host
	 */
	protected AbstractScenarioService(String fileName, Integer hostId, String mainHostIp) {
		this.guiController = new GUIControllerImpl();
		this.fileName = fileName;
		this.jadeRuntime = instance();

		executorService.execute(guiController);
		mainContainer = runAgentsContainer(hostId.toString(), mainHostIp);
	}

	protected File readFile(final String fileName) {
		URL resource = getClass().getClassLoader().getResource(RESOURCE_SCENARIO_PATH + fileName + ".xml");
		try {
			return new File(resource.toURI());
		} catch (URISyntaxException | NullPointerException e) {
			throw new InvalidScenarioException("Invalid scenario name.", e);
		}
	}

	protected ScenarioArgs parseScenario(File scenarioFile) {
		try {
			return xmlMapper.readValue(scenarioFile, ScenarioArgs.class);
		} catch (IOException e) {
			throw new InvalidScenarioException(format("Failed to parse scenario file \"%s\".xml.", fileName), e);
		}
	}

	protected AgentController runAgentController(AgentArgs args, ScenarioArgs scenario,
			AgentControllerFactory factory) {
		final AgentController agentController;
		try {
			agentController = factory.createAgentController(args);
			var agentNode = factory.createAgentNode(args, scenario);
			guiController.addAgentNodeToGraph(agentNode);
			agentController.putO2AObject(guiController, AgentController.ASYNC);
			agentController.putO2AObject(agentNode, AgentController.ASYNC);
			logger.info("Created {} agent.", args.getName());
		} catch (StaleProxyException e) {
			throw new JadeControllerException("Failed to run agent controller", e);
		}
		return agentController;
	}

	protected void runClientAgents(long agentsNumber, ScenarioArgs scenario, AgentControllerFactory factory) {
		var random = ThreadLocalRandom.current();
		LongStream.rangeClosed(1, agentsNumber).forEach(idx -> {
			final int randomPower = MIN_JOB_POWER + random.nextInt(MAX_JOB_POWER);
			final int randomStart = START_TIME_MIN + random.nextInt(START_TIME_MAX);
			final int randomEnd = randomStart + 1 + random.nextInt(END_TIME_MAX);
			final ClientAgentArgs clientAgentArgs =
					ImmutableClientAgentArgs.builder()
							.name(format("Client%d", idx))
							.jobId(String.valueOf(idx))
							.power(String.valueOf(randomPower))
							.start(String.valueOf(randomStart))
							.end(String.valueOf(randomEnd))
							.build();
			final AgentController agentController = runAgentController(clientAgentArgs, scenario, factory);
			try {
				agentController.start();
				agentController.activate();
				TimeUnit.MILLISECONDS.sleep(RUN_CLIENT_AGENT_PAUSE);
			} catch (StaleProxyException | InterruptedException e) {
				throw new JadeControllerException("Failed to run agent controller", e);
			}
		});
	}

	protected void runAgents(List<AgentController> controllers) {
		var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutor.schedule(() -> controllers.forEach(this::runAgent), GRAPH_INITIALIZATION_PAUSE, SECONDS);
		shutdownAndAwaitTermination(scheduledExecutor);
	}

	private void runAgent(AgentController controller) {
		try {
			controller.start();
			controller.activate();
			TimeUnit.MILLISECONDS.sleep(RUN_AGENT_PAUSE);
		} catch (StaleProxyException | InterruptedException e) {
			throw new JadeControllerException("Failed to run agent controller", e);
		}
	}

	private void shutdownAndAwaitTermination(ExecutorService executorService) {
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

	private ContainerController runMainController() throws ExecutionException, InterruptedException {
		final Profile profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, "Main-Container");
		profile.setParameter(Profile.MAIN_HOST, "localhost");
		profile.setParameter(Profile.MAIN_PORT, "6996");
		return executorService.submit(() -> jadeRuntime.createMainContainer(profile)).get();
	}

	protected ContainerController runAgentsContainer(String containerName, String host) {
		var profile = new ProfileImpl();
		profile.setParameter(Profile.CONTAINER_NAME, containerName);
		profile.setParameter(Profile.MAIN_HOST, host);
		profile.setParameter(Profile.MAIN_PORT, "6996");
		try {
			return executorService.submit(() -> jadeRuntime.createAgentContainer(profile)).get();
		} catch (InterruptedException | ExecutionException e) {
			if (containerName.equals(CLIENTS_CONTAINER_ID.toString())) {
				throw new JadeContainerException("Failed to create Agent Clients container", e);
			}
			throw new JadeContainerException("Failed to create CloudNetwork container", e);
		}
	}

	private void runJadeGui() throws StaleProxyException {
		final AgentController rma = mainContainer.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
		final AgentController sniffer = mainContainer.createNewAgent("sniffeur", "jade.tools.sniffer.Sniffer",
				new Object[0]);
		rma.start();
		sniffer.start();
	}
}