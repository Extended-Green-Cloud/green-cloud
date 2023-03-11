package com.greencloud.commons.message;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class MessageBuilder {

	private static final ObjectMapper MAPPER = new ObjectMapper()
			.registerModules(new GuavaModule())
			.registerModule(new JavaTimeModule());

	private ACLMessage aclMessage;

	private MessageBuilder() {
		aclMessage = new ACLMessage();
	}

	public static MessageBuilder builder() {
		return new MessageBuilder();
	}

	public MessageBuilder withMessageProtocol(final String messageProtocol) {
		this.aclMessage.setProtocol(messageProtocol);
		return this;
	}

	public MessageBuilder withConversationId(final String conversationId) {
		this.aclMessage.setConversationId(conversationId);
		return this;
	}

	public MessageBuilder withStringContent(final String content) {
		this.aclMessage.setContent(content);
		return this;
	}

	public MessageBuilder withObjectContent(final Object content) {
		try {
			this.aclMessage.setContent(MAPPER.writeValueAsString(content));
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public MessageBuilder withObjectContent(final Object content, final Consumer<Exception> errorHandler) {
		try {
			this.aclMessage.setContent(MAPPER.writeValueAsString(content));
		} catch (JsonProcessingException e) {
			errorHandler.accept(e);
		}
		return this;
	}

	public MessageBuilder withPerformative(final Integer performative) {
		this.aclMessage.setPerformative(performative);
		return this;
	}

	public MessageBuilder withReceivers(final AID... aids) {
		Arrays.stream(aids).forEach(aclMessage::addReceiver);
		return this;
	}

	public MessageBuilder withReceivers(final Collection<AID> aids) {
		aids.forEach(aclMessage::addReceiver);
		return this;
	}

	public MessageBuilder copy(final ACLMessage message) {
		aclMessage = (ACLMessage) message.clone();
		return this;
	}

	public ACLMessage build() {
		return aclMessage;
	}
}
