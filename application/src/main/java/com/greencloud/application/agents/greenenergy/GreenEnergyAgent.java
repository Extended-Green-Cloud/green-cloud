package com.greencloud.application.agents.greenenergy;

import static com.greencloud.application.common.constant.LoggingConstant.MDC_AGENT_NAME;
import static com.greencloud.application.yellowpages.YellowPagesService.register;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.GS_SERVICE_NAME;
import static com.greencloud.application.yellowpages.domain.DFServiceConstants.GS_SERVICE_TYPE;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.greencloud.application.agents.greenenergy.behaviour.cancellation.ListenForGreenEnergyJobCancellation;
import com.greencloud.application.agents.greenenergy.behaviour.weathercheck.monitor.ReportWeatherShortages;
import com.greencloud.application.agents.greenenergy.behaviour.powershortage.listener.ListenForServerPowerInformation;
import com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.ListenForPowerSupplyRequest;
import com.greencloud.application.agents.greenenergy.behaviour.powersupply.listener.ListenForPowerSupplyStatus;
import com.greencloud.application.agents.greenenergy.behaviour.sensor.SenseGreenSourceEvent;
import com.greencloud.application.agents.greenenergy.behaviour.weathercheck.request.RequestWeatherPeriodically;
import com.greencloud.application.agents.greenenergy.management.GreenEnergyAdaptationManagement;
import com.greencloud.application.agents.greenenergy.management.GreenEnergyStateManagement;
import com.greencloud.application.agents.greenenergy.management.GreenPowerManagement;
import com.greencloud.application.behaviours.ReceiveGUIController;
import com.greencloud.commons.agent.greenenergy.GreenEnergySourceTypeEnum;
import com.greencloud.commons.location.ImmutableLocation;

import jade.core.AID;
import jade.core.behaviours.Behaviour;

/**
 * Agent representing the Green Energy Source Agent that produces the power for the Servers
 */
public class GreenEnergyAgent extends AbstractGreenEnergyAgent {

	private static final Logger logger = LoggerFactory.getLogger(GreenEnergyAgent.class);

	private int scenarioMaximumCapacity;

	/**
	 * Method run at the agent's start. In initialize the Green Source Agent based on the given by the user arguments,
	 * registers it in the DF and then runs the starting behaviours - listening for the power requests and listening for
	 * the finish power request information.
	 */
	@Override
	protected void setup() {
		super.setup();
		MDC.put(MDC_AGENT_NAME, super.getLocalName());
		final Object[] args = getArguments();
		final AID ownerServer = new AID(args[1].toString(), AID.ISLOCALNAME);
		initializeAgent(args);
		register(this, GS_SERVICE_TYPE, GS_SERVICE_NAME, ownerServer.getName());
		addBehaviour(new ReceiveGUIController(this, behavioursRunAtStart()));
	}

	private void initializeAgent(final Object[] args) {
		if (Objects.nonNull(args) && args.length == 8) {
			this.monitoringAgent = new AID(args[0].toString(), AID.ISLOCALNAME);
			this.stateManagement = new GreenEnergyStateManagement(this);
			this.adaptationManagement = new GreenEnergyAdaptationManagement(this);
			try {
				this.scenarioMaximumCapacity = Integer.parseInt(args[2].toString());
				this.greenPowerManagement = new GreenPowerManagement(scenarioMaximumCapacity, this);
				this.pricePerPowerUnit = Double.parseDouble(args[3].toString());
				this.location = ImmutableLocation.builder()
						.latitude(Double.parseDouble(args[4].toString()))
						.longitude(Double.parseDouble(args[5].toString()))
						.build();
				if (args[6] instanceof String argument) {
					this.energyType = GreenEnergySourceTypeEnum.valueOf(argument);
				} else {
					this.energyType = (GreenEnergySourceTypeEnum) args[6];
				}
				this.weatherPredictionError = Double.parseDouble(args[7].toString());
			} catch (NumberFormatException e) {
				logger.info("Incorrect argument: please check arguments in the documentation");
				doDelete();
			}
		} else {
			logger.info("Incorrect arguments: some parameters for green source agent are missing - "
					+ "check the parameters in the documentation");
			doDelete();
		}
	}

	private List<Behaviour> behavioursRunAtStart() {
		return List.of(
				new ListenForPowerSupplyRequest(this),
				new ListenForPowerSupplyStatus(this),
				new SenseGreenSourceEvent(this),
				new ListenForServerPowerInformation(this),
				new RequestWeatherPeriodically(this),
				new ListenForGreenEnergyJobCancellation(),
				new ReportWeatherShortages(this)
		);
	}

	@Override
	protected void afterMove() {
		super.afterMove();
		this.greenPowerManagement = new GreenPowerManagement(scenarioMaximumCapacity, this);
		this.adaptationManagement = new GreenEnergyAdaptationManagement(this);
		this.stateManagement = new GreenEnergyStateManagement(this);
	}
}
