package runner.service;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.commons.args.agent.client.ClientAgentArgs;
import com.greencloud.commons.args.agent.client.ImmutableClientAgentArgs;
import com.greencloud.commons.args.event.EventArgs;
import com.greencloud.commons.args.event.newclient.NewClientEventArgs;
import com.greencloud.commons.args.event.newclient.PowerShortageEventArgs;
import com.gui.event.domain.PowerShortageEvent;

import jade.wrapper.AgentController;
import runner.domain.ScenarioEventsArgs;
import runner.factory.AgentControllerFactory;

/**
 * Service containing method used to handle events defined for the given scenario
 */
public class ScenarioEventService {

	private static final Logger logger = LoggerFactory.getLogger(ScenarioEventService.class);
	private final AbstractScenarioService scenarioService;
	private static final int POWER_SHORTAGE_START_DELAY = 1;

	/**
	 * Default constructor
	 *
	 * @param abstractScenarioService scenario service
	 */
	public ScenarioEventService(final AbstractScenarioService abstractScenarioService) {
		this.scenarioService = abstractScenarioService;
	}

	/**
	 * Method is responsible for reading and scheduling scenario events
	 *
	 * @param factory agent controller specified for events that require agents creation
	 */
	public void runScenarioEvents(final AgentControllerFactory factory) {
		final File scenarioEventsFile = scenarioService.readFile(scenarioService.scenarioEventsFileName);
		final ScenarioEventsArgs scenarioEvents = scenarioService.parseScenarioEvents(scenarioEventsFile);
		scheduleScenarioEvents(scenarioEvents.getEventArgs(), factory);
	}

	private void scheduleScenarioEvents(final List<EventArgs> eventArgs, final AgentControllerFactory factory) {
		logger.info("Scheduling scenario events...");
		final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
		eventArgs.forEach(event -> scheduledExecutor.schedule(() -> runEvent(event, factory), event.getOccurrenceTime(),
				SECONDS));
		scenarioService.shutdownAndAwaitTermination(scheduledExecutor);
	}

	private void runEvent(final EventArgs event, final AgentControllerFactory factory) {
		switch (event.getType()) {
			case NEW_CLIENT_EVENT -> runNewClientEvent(event, factory);
			case POWER_SHORTAGE_EVENT -> triggerPowerShortage(event);
		}
	}

	private void runNewClientEvent(final EventArgs event, final AgentControllerFactory factory) {
		final NewClientEventArgs newClientEvent = (NewClientEventArgs) event;
		final ClientAgentArgs clientAgentArgs = ImmutableClientAgentArgs.builder()
				.name(newClientEvent.getName())
				.jobId(newClientEvent.getJobId())
				.power(String.valueOf(newClientEvent.getPower()))
				.start(String.valueOf(newClientEvent.getStart()))
				.end(String.valueOf(newClientEvent.getEnd()))
				.build();
		final AgentController agentController = scenarioService.runAgentController(clientAgentArgs, null, factory);
		scenarioService.runAgent(agentController, 0);
	}

	private void triggerPowerShortage(final EventArgs event) {
		final PowerShortageEventArgs powerShortageArgs = (PowerShortageEventArgs) event;
		final Instant eventOccurrence = Instant.now().plusSeconds(POWER_SHORTAGE_START_DELAY);
		final PowerShortageEvent eventData = new PowerShortageEvent(eventOccurrence,
				powerShortageArgs.getNewMaximumCapacity(), powerShortageArgs.isFinished());
		scenarioService.guiController.triggerPowerShortageEvent(eventData, powerShortageArgs.getAgentName());
	}
}