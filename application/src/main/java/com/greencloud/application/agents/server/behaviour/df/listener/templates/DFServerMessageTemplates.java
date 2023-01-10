package com.greencloud.application.agents.server.behaviour.df.listener.templates;

import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.CONNECT_GREEN_SOURCE_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.DEACTIVATE_GREEN_SOURCE_PROTOCOL;
import static com.greencloud.application.messages.domain.constants.MessageProtocolConstants.DISCONNECT_GREEN_SOURCE_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.and;
import static jade.lang.acl.MessageTemplate.or;

import jade.lang.acl.MessageTemplate;

/**
 * Class stores all message templates used in listener behaviours for DF registration
 */
public class DFServerMessageTemplates {
	public static final MessageTemplate GREEN_SOURCE_CONNECTION_TEMPLATE = and(MatchPerformative(REQUEST),
			MatchProtocol(CONNECT_GREEN_SOURCE_PROTOCOL));
	public static final MessageTemplate GREEN_SOURCE_DISCONNECTION_TEMPLATE = and(MatchPerformative(REQUEST),
			or(MatchProtocol(DEACTIVATE_GREEN_SOURCE_PROTOCOL), MatchProtocol(DISCONNECT_GREEN_SOURCE_PROTOCOL)));
}