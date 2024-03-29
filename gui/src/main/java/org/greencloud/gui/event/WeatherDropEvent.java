package org.greencloud.gui.event;

import static org.greencloud.commons.mapper.JsonMapper.getMapper;
import static org.greencloud.commons.utils.time.TimeSimulation.getCurrentTime;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.greencloud.commons.args.agent.AgentType;
import org.greencloud.commons.constants.TimeConstants;
import org.greencloud.commons.exception.IncorrectMessageContentException;
import org.greencloud.gui.agents.regionalmanager.RegionalManagerNode;
import org.greencloud.gui.agents.egcs.EGCSNode;
import org.greencloud.gui.agents.scheduler.SchedulerNode;
import org.greencloud.gui.agents.server.ServerNode;
import org.greencloud.commons.enums.event.EventTypeEnum;
import org.greencloud.gui.messages.WeatherDropMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Getter;

/**
 * Event simulating long-term power shortage which affects given selected region of the system
 */
@Getter
public class WeatherDropEvent extends AbstractEvent {

	private static final Logger logger = LoggerFactory.getLogger(WeatherDropEvent.class);

	final long duration;

	/**
	 * Default event constructor
	 *
	 * @param occurrenceTime time when the event occurs
	 * @param duration       duration of weather drop
	 * @param agentName      name of the agent for which event was triggered
	 */
	public WeatherDropEvent(final Instant occurrenceTime, final long duration, final String agentName) {
		super(EventTypeEnum.WEATHER_DROP_EVENT, occurrenceTime, agentName);
		this.duration = duration;
	}

	public WeatherDropEvent(WeatherDropMessage weatherDropMessage) {
		this(getCurrentTime().plusSeconds(TimeConstants.SECONDS_PER_HOUR), weatherDropMessage.getData().getDuration(),
				weatherDropMessage.getAgentName());
	}

	/**
	 * Method creates event from the given message
	 *
	 * @param message received message
	 * @return WeatherDropEvent
	 */
	public static WeatherDropEvent create(String message) {
		final WeatherDropMessage weatherDropMessage = readWeatherDropMessage(message);
		return new WeatherDropEvent(weatherDropMessage);
	}

	private static WeatherDropMessage readWeatherDropMessage(String message) {
		try {
			return getMapper().readValue(message, WeatherDropMessage.class);
		} catch (JsonProcessingException e) {
			throw new IncorrectMessageContentException();
		}
	}

	@Override
	public void trigger(final Map<String, EGCSNode> agentNodes) {
		final RegionalManagerNode agentNode = (RegionalManagerNode) agentNodes.get(agentName);
		final SchedulerNode schedulerNode = (SchedulerNode) agentNodes.values().stream()
				.filter(node -> node.getAgentType().equals(AgentType.SCHEDULER.name()))
				.findFirst().orElseThrow();

		if (Objects.isNull(agentNode)) {
			logger.error("Agent {} was not found. Weather drop couldn't be triggered", agentName);
			return;
		}

		final List<EGCSNode> greenEnergyNodes = agentNode.getNodeArgs().getServerAgents().stream()
				.map(agentNodes::get)
				.map(ServerNode.class::cast)
				.map(server -> server.getNodeArgs().getGreenEnergyAgents())
				.flatMap(Collection::stream)
				.map(agentNodes::get)
				.toList();

		greenEnergyNodes.forEach(node -> node.addEvent(this));
		schedulerNode.addEvent(this);
		agentNode.addEvent(this);
	}
}
