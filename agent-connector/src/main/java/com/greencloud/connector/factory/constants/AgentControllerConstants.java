package com.greencloud.connector.factory.constants;

/**
 * Class stores constants used by the agent factories.
 *
 * <p> GRAPH_INITIALIZATION_DELAY - number of seconds after which graph is to be initialized </p>
 * <p> RUN_AGENT_DELAY - number of milliseconds after which next agent controller is to be run </p>
 * <p> RUN_CLIENT_AGENT_DELAY - number of milliseconds after which next client agent controller is to be run </p>
 */
public class AgentControllerConstants {

	public static final Long GRAPH_INITIALIZATION_DELAY = 7L;
	public static final Integer RUN_AGENT_DELAY = 100;
	public static final Integer RUN_CLIENT_AGENT_DELAY = 1000;
}
