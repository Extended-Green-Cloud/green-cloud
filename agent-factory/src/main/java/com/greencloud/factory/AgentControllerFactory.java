package com.greencloud.factory;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.greencloud.commons.args.agent.AgentArgs;
import com.greencloud.commons.scenario.ScenarioStructureArgs;

import jade.wrapper.AgentController;

/**
 * Factory used to create and run agent controllers
 */
public interface AgentControllerFactory {

	/**
	 * Method creates the agent controllers
	 *
	 * @param agentArgs agent arguments
	 * @param scenario  which has to be passed to managing agent
	 * @return AgentController that can be started
	 */
	AgentController createAgentController(AgentArgs agentArgs, ScenarioStructureArgs scenario);

	/**
	 * Method runs the agent controllers
	 *
	 * @param controllers controllers that are to be run
	 */
	void runAgentControllers(final List<AgentController> controllers, final long agentRunDelay);

	/**
	 * Method runs single agent controller
	 *
	 * @param controller controller that is to be run
	 */
	void runAgentController(final AgentController controller, final long agentRunDelay);

	/**
	 * Method used handle runnable execution termination
	 *
	 * @param executorService executor service that runs given process
	 */
	void shutdownAndAwaitTermination(final ExecutorService executorService);
}