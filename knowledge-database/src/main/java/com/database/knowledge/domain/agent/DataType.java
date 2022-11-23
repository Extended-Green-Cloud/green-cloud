package com.database.knowledge.domain.agent;

import com.database.knowledge.domain.agent.client.ClientMonitoringData;
import com.database.knowledge.domain.agent.greensource.WeatherShortages;
import com.database.knowledge.domain.agent.monitoring.ProcessedApiRequest;
import com.database.knowledge.domain.agent.server.ServerMonitoringData;

public enum DataType {

	DEFAULT(MonitoringData.class),
	CLIENT_MONITORING(ClientMonitoringData.class),
	SERVER_MONITORING(ServerMonitoringData.class),
	WEATHER_SHORTAGES(WeatherShortages.class),
	HEALTH_CHECK(HealthCheck.class),
	PROCESSED_API_REQUEST(ProcessedApiRequest.class);

	private final Class<? extends MonitoringData> dataTypeClass;

	DataType(Class<? extends MonitoringData> dataTypeClass) {
		this.dataTypeClass = dataTypeClass;
	}

	public Class<? extends MonitoringData> getDataTypeClass() {
		return dataTypeClass;
	}
}
