package com.greencloud.application.agents.greenenergy;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.greencloud.application.agents.AbstractAgent;
import com.greencloud.application.agents.greenenergy.domain.GreenEnergySourceTypeEnum;
import com.greencloud.application.agents.greenenergy.management.GreenEnergyStateManagement;
import com.greencloud.application.agents.greenenergy.management.GreenPowerManagement;
import com.greencloud.application.domain.MonitoringData;
import com.greencloud.application.domain.job.JobStatusEnum;
import com.greencloud.application.domain.job.PowerJob;
import com.greencloud.application.domain.location.Location;

import jade.core.AID;

/**
 * Abstract agent class storing data of the Green Source Energy Agent
 */
public abstract class AbstractGreenEnergyAgent extends AbstractAgent {

	protected transient GreenPowerManagement greenPowerManagement;
	protected transient GreenEnergyStateManagement stateManagement;
	protected transient Location location;
	protected GreenEnergySourceTypeEnum energyType;
	protected double pricePerPowerUnit;
	protected transient Map<PowerJob, JobStatusEnum> powerJobs;
	protected AID monitoringAgent;
	protected AID ownerServer;

	AbstractGreenEnergyAgent() {
		super.setup();
		this.powerJobs = new ConcurrentHashMap<>();
	}

	public AID getOwnerServer() {
		return ownerServer;
	}

	public double getPricePerPowerUnit() {
		return pricePerPowerUnit;
	}

	public void setPricePerPowerUnit(double pricePerPowerUnit) {
		this.pricePerPowerUnit = pricePerPowerUnit;
	}

	public Double getCapacity(MonitoringData weather, Instant startTime) {
		final double availablePower = greenPowerManagement.getAvailablePower(weather, startTime);
		return availablePower > getMaximumCapacity() ? getMaximumCapacity() : availablePower;
	}

	public int getMaximumCapacity() {
		return this.greenPowerManagement.getCurrentMaximumCapacity();
	}

	public void setMaximumCapacity(int maximumCapacity) {
		this.greenPowerManagement.setCurrentMaximumCapacity(maximumCapacity);
	}

	public int getInitialMaximumCapacity() {
		return this.greenPowerManagement.getInitialMaximumCapacity();
	}

	public Map<PowerJob, JobStatusEnum> getPowerJobs() {
		return powerJobs;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public AID getMonitoringAgent() {
		return monitoringAgent;
	}

	public void setMonitoringAgent(AID monitoringAgent) {
		this.monitoringAgent = monitoringAgent;
	}

	public void setGreenPowerManagement(GreenPowerManagement greenPowerManagement) {
		this.greenPowerManagement = greenPowerManagement;
	}

	public GreenEnergySourceTypeEnum getEnergyType() {
		return energyType;
	}

	public GreenEnergyStateManagement manage() {
		return stateManagement;
	}
}