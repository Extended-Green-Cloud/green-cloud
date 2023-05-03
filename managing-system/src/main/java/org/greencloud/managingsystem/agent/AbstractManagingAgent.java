package org.greencloud.managingsystem.agent;

import static com.greencloud.commons.agent.AgentType.MANAGING;

import java.util.List;
import java.util.Map;

import org.greencloud.managingsystem.service.analyzer.AnalyzerService;
import org.greencloud.managingsystem.service.executor.ExecutorService;
import org.greencloud.managingsystem.service.mobility.MobilityService;
import org.greencloud.managingsystem.service.monitoring.MonitoringService;
import org.greencloud.managingsystem.service.planner.PlannerService;

import com.database.knowledge.domain.goal.AdaptationGoal;
import com.greencloud.application.agents.AbstractAgent;
import com.greencloud.commons.scenario.ScenarioStructureArgs;

import jade.core.AID;
import jade.core.Location;
import jade.wrapper.ContainerController;

/**
 * Abstract agent class storing data of the Managing Agent
 */
public abstract class AbstractManagingAgent extends AbstractAgent {

	protected ScenarioStructureArgs greenCloudStructure;
	protected ContainerController greenCloudController;
	protected Map<Location, AID> containersLocations;

	protected List<AdaptationGoal> adaptationGoalList;
	protected double systemQualityThreshold;

	protected MonitoringService monitoringService;
	protected AnalyzerService analyzerService;
	protected PlannerService plannerService;
	protected ExecutorService executorService;
	protected MobilityService mobilityService;

	/**
	 * Default constructor
	 */
	protected AbstractManagingAgent() {
		super();
		agentType = MANAGING;
	}

	public ScenarioStructureArgs getGreenCloudStructure() {
		return greenCloudStructure;
	}

	public MonitoringService monitor() {
		return monitoringService;
	}

	public AnalyzerService analyze() {
		return analyzerService;
	}

	public PlannerService plan() {
		return plannerService;
	}

	public ExecutorService execute() {
		return executorService;
	}
	public MobilityService move() {return  mobilityService;}

	public double getSystemQualityThreshold() {
		return systemQualityThreshold;
	}

	public List<AdaptationGoal> getAdaptationGoalList() {
		return adaptationGoalList;
	}

	public void setAdaptationGoalList(List<AdaptationGoal> adaptationGoalList) {
		this.adaptationGoalList = adaptationGoalList;
	}

	/**
	 * @return green cloud controller, used to modify green cloud structure, add/remove agents
	 */
	public ContainerController getGreenCloudController() {
		return greenCloudController;
	}

	/**
	 * @return locations of all agent containers present in the system
	 */
	public Map<Location, AID> getContainersLocations() {
		return containersLocations;
	}

	public void setContainersLocations(Map<Location, AID> containersLocations) {
		this.containersLocations = containersLocations;
	}
}
