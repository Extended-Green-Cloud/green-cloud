package org.greencloud.agentsystem.agents.cloudnetwork;

import org.greencloud.agentsystem.agents.AbstractAgent;

import org.greencloud.commons.args.agent.cloudnetwork.agent.CloudNetworkAgentProps;
import com.gui.agents.cloudnetwork.CloudNetworkNode;

/**
 * Abstract agent class storing the data regarding Cloud Network Agent
 */
public abstract class AbstractCloudNetworkAgent extends AbstractAgent<CloudNetworkNode, CloudNetworkAgentProps> {

	AbstractCloudNetworkAgent() {
		super();
		this.properties = new CloudNetworkAgentProps(getName());
	}
}