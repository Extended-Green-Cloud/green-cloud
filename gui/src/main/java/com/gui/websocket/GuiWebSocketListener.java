package com.gui.websocket;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gui.agents.AbstractAgentNode;
import com.gui.event.domain.PowerShortageEvent;
import com.gui.message.PowerShortageMessage;

public class GuiWebSocketListener extends GuiWebSocketClient {

	private static final Logger logger = LoggerFactory.getLogger(GuiWebSocketListener.class);

	private final Map<String, AbstractAgentNode> agentNodes;

	public GuiWebSocketListener(URI serverUri) {
		super(serverUri);
		agentNodes = new HashMap<>();
	}

	public void addAgentNode(AbstractAgentNode agentNode) {
		agentNodes.put(agentNode.getAgentName(), agentNode);
	}

	/**
	 * Method triggers power shortage event in the specified agent
	 *
	 * @param powerShortageEvent data for the power shortage event
	 * @param agentName          agent for which the event is triggered
	 */
	public void triggerPowerShortage(PowerShortageEvent powerShortageEvent, String agentName) {
		AbstractAgentNode agentNode = agentNodes.get(agentName);
		agentNode.addEvent(powerShortageEvent);
	}

	@Override
	public void onOpen(ServerHandshake serverHandshake) {
		logger.info("Connected to event listener");
	}

	@Override
	public void onMessage(String message) {
		logger.info("Received event: {}", message);
		if (message.contains("POWER_SHORTAGE_EVENT")) {
			handlePowerShortageMessage(message);
		}
	}

	private void handlePowerShortageMessage(String message) {
		PowerShortageMessage powerShortageMessage = readPowerShortage(message);
		PowerShortageEvent powerShortageEvent = new PowerShortageEvent(powerShortageMessage);
		triggerPowerShortage(powerShortageEvent, powerShortageMessage.getAgentName());
	}

	private PowerShortageMessage readPowerShortage(String message) {
		try {
			return mapper.readValue(message, PowerShortageMessage.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}