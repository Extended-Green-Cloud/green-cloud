package com.greencloud.application.agents.greenenergy.management;

import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.time.ZoneOffset.UTC;
import static java.util.Comparator.comparingLong;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.shredzone.commons.suncalc.SunTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greencloud.application.agents.greenenergy.GreenEnergyAgent;
import com.greencloud.application.agents.greenenergy.domain.GreenEnergyAgentConstants;
import com.greencloud.application.agents.greenenergy.management.logs.GreenEnergyManagementLog;
import com.greencloud.application.domain.MonitoringData;
import com.greencloud.application.domain.WeatherData;
import com.greencloud.application.domain.location.Location;

/**
 * Set of methods used in green power management
 */
public class GreenPowerManagement {

	private static final Logger logger = LoggerFactory.getLogger(GreenPowerManagement.class);

	private final GreenEnergyAgent greenEnergyAgent;
	private final int initialMaximumCapacity;
	private int currentMaximumCapacity;

	/**
	 * Class constructor
	 *
	 * @param maximumCapacity  - maximum capacity available at given source
	 * @param greenEnergyAgent - agent representing given source
	 */
	public GreenPowerManagement(final int maximumCapacity, final GreenEnergyAgent greenEnergyAgent) {
		this.currentMaximumCapacity = maximumCapacity;
		this.initialMaximumCapacity = maximumCapacity;
		this.greenEnergyAgent = greenEnergyAgent;
	}

	/**
	 * Function computes available power, based on given com.greencloud.application.weather and time, for specific type of energy source
	 *
	 * @param weather  - com.greencloud.application.weather information
	 * @param dateTime - time when the power will be used
	 * @return power in Watts
	 */
	public double getAvailablePower(WeatherData weather, ZonedDateTime dateTime) {
		return switch (greenEnergyAgent.getEnergyType()) {
			case SOLAR -> getSolarPower(weather, dateTime, greenEnergyAgent.getLocation());
			case WIND -> getWindPower(weather);
		};
	}

	/**
	 * Function computes available power, based on retrieved monitoring data and time
	 *
	 * @param monitoringData - com.greencloud.application.weather information
	 * @param dateTime       - time when the power will be used
	 * @return power in Watts
	 */
	public double getAvailablePower(MonitoringData monitoringData, Instant dateTime) {
		var weather = monitoringData.getDataForTimestamp(dateTime)
				.orElse(getNearestWeather(monitoringData, dateTime));
		return getAvailablePower(weather, dateTime.atZone(UTC));
	}

	/**
	 * @param currentMaximumCapacity - new maximum capacity
	 */
	public void setCurrentMaximumCapacity(int currentMaximumCapacity) {
		this.currentMaximumCapacity = currentMaximumCapacity;
	}

	/**
	 * @return initial maximum capacity
	 */
	public int getInitialMaximumCapacity() {
		return initialMaximumCapacity;
	}

	/**
	 * @return current maximum capacity
	 */
	public int getCurrentMaximumCapacity() {
		return currentMaximumCapacity;
	}

	private double getWindPower(WeatherData weather) {
		return currentMaximumCapacity * pow(
				(weather.getWindSpeed() + 5 - GreenEnergyAgentConstants.CUT_ON_WIND_SPEED) / (
						GreenEnergyAgentConstants.RATED_WIND_SPEED - GreenEnergyAgentConstants.CUT_ON_WIND_SPEED), 2)
				* GreenEnergyAgentConstants.TEST_MULTIPLIER;
	}

	private double getSolarPower(WeatherData weather, ZonedDateTime dateTime, Location location) {
		var sunTimes = getSunTimes(dateTime, location);
		var dayTime = dateTime.toLocalTime();
		if (!GreenEnergyAgentConstants.MOCK_SOLAR_ENERGY || (dayTime.isBefore(Objects.requireNonNull(sunTimes.getRise()).toLocalTime()) ||
				dayTime.isAfter(Objects.requireNonNull(sunTimes.getSet()).toLocalTime()))) {
			logger.debug(GreenEnergyManagementLog.SOLAR_FARM_SHUTDOWN_LOG, dateTime, sunTimes.getRise(), sunTimes.getSet());
			return 0;
		}

		return currentMaximumCapacity * min(weather.getCloudCover() / 100 + 0.1, 1) * GreenEnergyAgentConstants.TEST_MULTIPLIER;
	}

	private SunTimes getSunTimes(ZonedDateTime dateTime, Location location) {
		return SunTimes.compute().on(dateTime).at(location.getLatitude(), location.getLongitude()).execute();
	}

	private WeatherData getNearestWeather(MonitoringData monitoringData, Instant timestamp) {
		return monitoringData.getWeatherData().stream()
				.min(comparingLong(i -> Math.abs(i.getTime().getEpochSecond() - timestamp.getEpochSecond())))
				.orElseThrow(() -> new NoSuchElementException("No value present"));
	}
}