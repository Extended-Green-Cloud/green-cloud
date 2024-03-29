package com.database.knowledge.domain.agent.greensource;

import org.immutables.value.Value;

import com.database.knowledge.domain.agent.NetworkComponentMonitoringData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Interface storing monitoring data sent by the Green Source agent
 */
@Value.Immutable
@JsonSerialize(as = ImmutableGreenSourceMonitoringData.class)
@JsonDeserialize(as = ImmutableGreenSourceMonitoringData.class)
public interface GreenSourceMonitoringData extends NetworkComponentMonitoringData {

	/**
	 * @return current weather prediction error taking into account during available power calculation
	 */
	double getWeatherPredictionError();

	/**
	 * @return flag indicating if a green source is currently ongoing some server disconnection adaptation
	 */
	boolean isBeingDisconnected();
}
