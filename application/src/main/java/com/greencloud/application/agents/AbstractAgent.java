package com.greencloud.application.agents;

import static com.greencloud.application.messages.factory.AgentDiscoveryMessageFactory.prepareMessageToManagingAgent;
import static com.greencloud.commons.agent.AgentType.CLIENT;
import static com.greencloud.commons.agent.AgentType.MANAGING;
import static com.greencloud.commons.constants.LoggingConstant.MDC_AGENT_NAME;
import static com.greencloud.commons.constants.LoggingConstant.MDC_CLIENT_NAME;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.database.knowledge.domain.action.AdaptationActionEnum;
import com.database.knowledge.domain.agent.DataType;
import com.database.knowledge.domain.agent.MonitoringData;
import com.greencloud.application.behaviours.ReceiveGUIController;
import com.greencloud.application.domain.agent.enums.AgentManagementEnum;
import com.greencloud.commons.agent.AgentType;
import com.greencloud.commons.exception.JadeContainerException;
import com.greencloud.commons.managingsystem.planner.AdaptationActionParameters;
import com.gui.agents.AbstractAgentNode;
import com.gui.controller.GuiController;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.ControllerException;

/**
 * Abstract class representing agent which has the connection with GUI controller
 */
public abstract class AbstractAgent extends Agent {

	private static final Logger logger = LoggerFactory.getLogger(AbstractAgent.class);

	protected AgentType agentType;
	protected AID parentDFAddress;
	protected GuiController guiController;
	protected AbstractAgentNode agentNode;
	protected transient Map<AgentManagementEnum, AbstractAgentManagement> agentManagementServices;
	protected ParallelBehaviour mainBehaviour;

	protected AbstractAgent() {
		setEnabledO2ACommunication(true, 2);
		this.agentManagementServices = new EnumMap<>(AgentManagementEnum.class);
	}

	/**
	 * Abstract method used to validate if arguments of the given agent are correct
	 */
	protected void validateAgentArguments() {
	}

	/**
	 * Abstract method used to initialize given agent data
	 *
	 * @param arguments arguments passed by the user
	 */
	protected void initializeAgent(final Object[] arguments) {
	}

	/**
	 * Abstract method used to initialize agent management services
	 */
	protected void initializeAgentManagements() {
	}

	/**
	 * Abstract method that is used to prepare starting behaviours for given agent
	 */
	protected List<Behaviour> prepareStartingBehaviours() {
		return emptyList();
	}

	/**
	 * Abstract method responsible for running starting behaviours
	 */
	protected void runStartingBehaviours() {
		addBehaviour(new ReceiveGUIController(this, prepareStartingBehaviours()));
	}

	/**
	 * Abstract method invoked when the agent is the target of adaptation
	 *
	 * @param adaptationActionEnum adaptation action type
	 * @param actionParameters     parameters related with given adaptation
	 * @return flag indicating if adaptation was successful
	 */
	public boolean executeAction(final AdaptationActionEnum adaptationActionEnum,
			final AdaptationActionParameters actionParameters) {
		// this method must be overwritten in agent types that will be a target to adaptation
		throw new UnsupportedOperationException();
	}

	/**
	 * Abstract method invoked when the agent is the target of adaptation and the adaptation requires communicating
	 * with other agents (i.e. cannot be executed on the spot)
	 *
	 * @param adaptationActionEnum adaptation action type
	 * @param actionParameters     parameters related with given adaptation
	 * @param adaptationMessage    message with adaptation request
	 */
	public void executeAction(final AdaptationActionEnum adaptationActionEnum,
			final AdaptationActionParameters actionParameters,
			final ACLMessage adaptationMessage) {
		// this method can be overwritten in agent types that will be a target to adaptation
		throw new UnsupportedOperationException();
	}

	@Override
	public void clean(boolean ok) {
		if (!ok && nonNull(getAgentNode()) && !agentType.equals(CLIENT)) {
			getAgentNode().removeAgentNodeFromGraph();
		}
		super.clean(ok);
	}

	@Override
	protected void setup() {
		logger.info("Setting up Agent {}", getName());
		if (agentType.equals(CLIENT)) {
			MDC.put(MDC_CLIENT_NAME, super.getLocalName());
		} else {
			MDC.put(MDC_AGENT_NAME, super.getLocalName());
		}
		final Object[] arguments = getArguments();

		initializeAgent(arguments);
		validateAgentArguments();
		runStartingBehaviours();
		initializeAgentManagements();

		// checking if the managing agent should be informed about agent creation
		if (arguments.length > 0 && !List.of(CLIENT, MANAGING).contains(agentType)
				&& (boolean) arguments[arguments.length - 2]) {
			try {
				final AID managingAgent = (AID) arguments[arguments.length - 1];
				send(prepareMessageToManagingAgent(getContainerController().getContainerName(), getLocalName(),
						managingAgent));
			} catch (ControllerException e) {
				throw new JadeContainerException("Container not found!", e);
			}
		}
	}

	@Override
	protected void takeDown() {
		logger.info("I'm finished. Bye!");
		super.takeDown();
	}

	@Override
	protected void afterMove() {
		super.afterMove();
		initializeAgentManagements();
	}

	@Override
	public void addBehaviour(Behaviour b) {
		if (nonNull(mainBehaviour) && !mainBehaviour.equals(b)) {
			mainBehaviour.addSubBehaviour(b);
		} else {
			super.addBehaviour(b);
		}
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public AID getParentDFAddress() {
		return parentDFAddress;
	}

	public AbstractAgentNode getAgentNode() {
		return agentNode;
	}

	public void setAgentNode(AbstractAgentNode agentNode) {
		this.agentNode = agentNode;
	}

	public GuiController getGuiController() {
		return guiController;
	}

	public void setGuiController(GuiController guiController) {
		this.guiController = guiController;
	}

	public void setMainBehaviour(ParallelBehaviour mainBehaviour) {
		this.mainBehaviour = mainBehaviour;
	}

	public void writeMonitoringData(DataType dataType, MonitoringData monitoringData) {
		if (nonNull(agentNode)) {
			agentNode.getDatabaseClient().writeMonitoringData(this.getAID().getName(), dataType, monitoringData);
		}
	}

	/**
	 * Method used primarily in testing
	 */
	public void addAgentManagement(final AbstractAgentManagement management, final AgentManagementEnum type) {
		agentManagementServices.put(type, management);
	}
}
