package com.greencloud.application.agents.greenenergy.behaviour.weathercheck.listener.logs;

/**
 * Class contains all constants dedicated to logging messages in com.greencloud.application.weather checking listener behaviours
 */
public class WeatherCheckListenerLog {

	// WEATHER DATA LISTENER LOG MESSAGES
	public static final String WEATHER_UNAVAILABLE_JOB_LOG = "[{}] The data for the job is not available. Leaving job {} on hold";
	public static final String WEATHER_UNAVAILABLE_LOG = "[{}] The com.greencloud.application.weather data is not available at {}";
	public static final String NO_POWER_LEAVE_ON_HOLD_LOG = "[{}] There is not enough available power to put job back in progress. Leaving the job {} on hold";
	public static final String CHANGE_JOB_STATUS_LOG = "[{}] Changing the status of the job {}";
	public static final String POWER_DROP_LOG = "[{}] Received the com.greencloud.application.weather data at {}. There was a power drop! Scheduling job transferring behaviour!";
	public static final String NO_POWER_DROP_LOG = "[{}] Received the com.greencloud.application.weather data at {}. Power has not dropped. Continuing jobs execution";

	// NEW JOB WEATHER DATA LISTENER LOG MESSAGES
	public static final String INCORRECT_WEATHER_DATA_FORMAT_LOG = "[{}] I didn't understand the response with the com.greencloud.application.weather data, sending refuse message to server";
	public static final String WEATHER_UNAVAILABLE_FOR_JOB_LOG = "[{}] Weather data not available, sending refuse message to server.";
	public static final String TOO_BAD_WEATHER_LOG = "[{}] Too bad com.greencloud.application.weather conditions, sending refuse message to server for job with id {}.";
	public static final String NOT_ENOUGH_POWER_LOG = "[{}] Refusing job with id {} - not enough available power. Needed {}, available {}";
	public static final String POWER_SUPPLY_PROPOSAL_LOG = "[{}] Replying with propose message to server for job with id {}.";

}