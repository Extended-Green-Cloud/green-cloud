package com.greencloud.application.agents.cloudnetwork;

import static com.greencloud.application.common.constant.LoggingConstant.MDC_AGENT_NAME;
import static com.greencloud.application.yellowpages.YellowPagesService.register;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.CNA_SERVICE_NAME;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.CNA_SERVICE_TYPE;

import java.util.Collections;
import java.util.List;

import com.greencloud.application.agents.cloudnetwork.management.CloudNetworkMonitorManagement;
import org.slf4j.MDC;

import com.greencloud.application.agents.cloudnetwork.behaviour.df.FindSchedulerAndServerAgents;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.ListenForScheduledJob;
import com.greencloud.application.agents.cloudnetwork.behaviour.jobhandling.listener.ListenForJobStatusChange;
import com.greencloud.application.agents.cloudnetwork.behaviour.powershortage.listener.ListenForServerJobTransferRequest;
import com.greencloud.application.agents.cloudnetwork.management.CloudNetworkStateManagement;
import com.greencloud.application.behaviours.ReceiveGUIController;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;

/**
 * Agent representing the Cloud Network Agent that handles part of the Cloud Network
 */
public class CloudNetworkAgent extends AbstractCloudNetworkAgent {

	/**
	 * Method run at the agent's start. In initialize the Cloud Network Agent based on the given by the user arguments and
	 * runs the starting behaviours - looking up the corresponding servers and listening for the job requests.
	 */
	@Override
	protected void setup() {
		super.setup();
		MDC.put(MDC_AGENT_NAME, super.getLocalName());
		initializeAgent();
		addBehaviour(new ReceiveGUIController(this, prepareBehaviours()));
	}

	@Override
	protected void takeDown() {
		super.takeDown();
	}

	private void initializeAgent() {
		register(this, CNA_SERVICE_TYPE, CNA_SERVICE_NAME);
		this.stateManagement = new CloudNetworkStateManagement(this);
		this.monitorManagement = new CloudNetworkMonitorManagement(this);
	}

	private List<Behaviour> prepareBehaviours() {
		final ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
		parallelBehaviour.addSubBehaviour(prepareStartingBehaviour());
		parallelBehaviour.addSubBehaviour(new ListenForJobStatusChange());
		parallelBehaviour.addSubBehaviour(new ListenForServerJobTransferRequest());
		return Collections.singletonList(parallelBehaviour);
	}

	private SequentialBehaviour prepareStartingBehaviour() {
		var startingBehaviour = new SequentialBehaviour(this);
		startingBehaviour.addSubBehaviour(new FindSchedulerAndServerAgents());
		startingBehaviour.addSubBehaviour(new ListenForScheduledJob());
		return startingBehaviour;
	}
}
