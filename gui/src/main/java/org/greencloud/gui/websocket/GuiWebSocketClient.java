package org.greencloud.gui.websocket;

import static org.greencloud.commons.constants.TimeConstants.SECONDS_PER_HOUR;
import static org.greencloud.commons.mapper.JsonMapper.getMapper;

import java.net.URI;
import java.time.Instant;

import org.greencloud.commons.exception.IncorrectMessageContentException;
import org.greencloud.gui.messages.ImmutableReportSystemStartTimeMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

public class GuiWebSocketClient extends WebSocketClient {

	private static final Logger logger = LoggerFactory.getLogger(GuiWebSocketClient.class);

	public GuiWebSocketClient(URI serverUri) {
		super(serverUri);
	}

	public void send(Object message) {
		try {
			if (super.isOpen()) {
				super.send(getMapper().writeValueAsString(message));
			}
		} catch (JsonProcessingException e) {
			throw new IncorrectMessageContentException();
		}
	}

	@Override
	public void onOpen(ServerHandshake serverHandshake) {
		logger.info("Connected to WebSocket server");
	}

	@Override
	public void onMessage(String message) {
		logger.debug("Received message from WebSocket server: {}", message);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		logger.warn("Connection closed by {}, code: {}, reason: {} ", remote ? "remote peer" : "us", code, reason);
	}

	@Override
	public void onError(Exception e) {
		logger.error("WebSocket error! {}", e.getMessage());
	}

	@Override
	public void connect() {
		if (!isOpen()) {
			super.connect();
		}
	}

	/**
	 * Method sends the information about simulation start time to a given Websocket server
	 *
	 * @param time time when the simulation has started
	 */
	public void reportSystemStartTime(final Instant time) {
		this.send(ImmutableReportSystemStartTimeMessage.builder()
				.time(time.toEpochMilli())
				.secondsPerHour(SECONDS_PER_HOUR)
				.build());
	}
}
