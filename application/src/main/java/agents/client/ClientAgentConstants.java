package agents.client;

import java.time.temporal.ValueRange;

/**
 * Class storing Cloud Network Agent constants:
 * <p>
 * CLOUD_NETWORK_AGENTS -   data store key under which the Cloud Network Agents found in the DF will be stored
 * MAX_TRAFFIC_DIFFERENCE - value range describing the maximum difference in power in use for network segment that can be
 * neglected in Cloud Network selection
 */
public class ClientAgentConstants {
    public static final ValueRange MAX_TRAFFIC_DIFFERENCE = ValueRange.of(-2, 2);
    public static final ValueRange MAX_TIME_DIFFERENCE = ValueRange.of(-500, 500);
    public static final String CLOUD_NETWORK_AGENTS = "CLOUD_NETWORK_AGENTS_LIST";
}
