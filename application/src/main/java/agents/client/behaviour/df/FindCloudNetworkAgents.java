package agents.client.behaviour.df;

import static agents.client.behaviour.df.logs.ClientDFLog.NO_CLOUD_NETWORKS_FOUND_LOG;
import static agents.client.domain.ClientAgentConstants.CLOUD_NETWORK_AGENTS;
import static utils.GUIUtils.announceNewClient;
import static yellowpages.YellowPagesService.search;
import static yellowpages.domain.DFServiceConstants.CNA_SERVICE_TYPE;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import agents.client.ClientAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

/**
 * Behaviour finds cloud network agents for communication
 */
public class FindCloudNetworkAgents extends OneShotBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(FindCloudNetworkAgents.class);
	private ClientAgent myClientAgent;
	private String guid;

	/**
	 * Method casts the abstract agent to agent of type ClientAgent.
	 */
	@Override
	public void onStart() {
		super.onStart();
		this.myClientAgent = (ClientAgent) myAgent;
		this.guid = myAgent.getName();
	}

	/**
	 * Method looks for Cloud Network Agents and puts the retrieved result in data store.
	 */
	@Override
	public void action() {
		final List<AID> cloudNetworkAgents = search(myAgent, CNA_SERVICE_TYPE);
		if (cloudNetworkAgents.isEmpty()) {
			logger.info(NO_CLOUD_NETWORKS_FOUND_LOG, guid);
			myClientAgent.doDelete();
			return;
		}
		if (!myClientAgent.isAnnounced()) {
			announceNewClient(myClientAgent);
			myClientAgent.announce();
		}
		getParent().getDataStore().put(CLOUD_NETWORK_AGENTS, cloudNetworkAgents);
	}
}
