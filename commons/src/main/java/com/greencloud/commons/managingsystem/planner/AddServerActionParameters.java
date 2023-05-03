package com.greencloud.commons.managingsystem.planner;

import java.util.List;

import com.greencloud.commons.args.agent.AgentArgs;
import com.greencloud.commons.args.agent.greenenergy.GreenEnergyAgentArgs;
import com.greencloud.commons.args.agent.monitoring.MonitoringAgentArgs;
import com.greencloud.commons.args.agent.server.ServerAgentArgs;

import jade.core.AID;
import jade.core.Location;

public class AddServerActionParameters implements SystemAdaptationActionParameters {

	/**
	 * Arguments of the server agent that should be added to the system within the adaptation action.
	 */
	private final ServerAgentArgs serverAgentArgs;
	/**
	 * Arguments of the green energy source agent that should be added to the system within the adaptation action.
	 */
	private final GreenEnergyAgentArgs greenEnergyAgentArgs;
	/**
	 * Arguments of the green energy source agent that should be added to the system within the adaptation action.
	 */
	private final MonitoringAgentArgs monitoringAgentArgs;
	/**
	 * In case of multi container scenario location to which the newly created agents should be moved.
	 */
	private final Location agentsTargetLocation;
	/**
	 * In case of multi-platform scenario location to which the newly created agents should be moved.
	 */
	private final AID agentsTargetAMS;

	public AddServerActionParameters(ServerAgentArgs serverAgentArgs, GreenEnergyAgentArgs greenEnergyAgentArgs,
			MonitoringAgentArgs monitoringAgentArgs, Location agentsTargetLocation, AID agentsTargetAMS) {
		this.serverAgentArgs = serverAgentArgs;
		this.greenEnergyAgentArgs = greenEnergyAgentArgs;
		this.monitoringAgentArgs = monitoringAgentArgs;
		this.agentsTargetLocation = agentsTargetLocation;
		this.agentsTargetAMS = agentsTargetAMS;
	}

	@Override
	public List<AgentArgs> getAgentsArguments() {
		return List.of(monitoringAgentArgs, greenEnergyAgentArgs, serverAgentArgs);
	}

	@Override
	public Location getAgentsTargetLocation() {
		return agentsTargetLocation;
	}

	@Override
	public AID getAgentsTargetAMS() {
		return agentsTargetAMS;
	}
}
