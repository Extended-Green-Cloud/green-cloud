package com.greencloud.commons.args.greenenergy;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.greencloud.commons.args.AgentArgs;
import com.greencloud.commons.location.Location;

@JsonDeserialize(as = ImmutableGreenEnergyNodeArgs.class)
@JsonSerialize(as = ImmutableGreenEnergyNodeArgs.class)
@Value.Immutable
public interface GreenEnergyNodeArgs extends AgentArgs {

	/**
	 * @return owned monitoring agent name
	 */
	String getMonitoringAgent();

	/**
	 * @return owner server name
	 */
	String getServerAgent();

	/**
	 * @return agent location
	 */
	Location getAgentLocation();

	/**
	 * @return maximum capacity of the server
	 */
	String getMaximumCapacity();

	/**
	 * @return type of energy source
	 */
	String getEnergyType();
}