package agents.greenenergy.behaviour.powercheck;

import static utils.GUIUtils.displayMessageArrow;
import static mapper.JsonMapper.getMapper;

import agents.greenenergy.GreenEnergyAgent;

import com.fasterxml.jackson.core.JsonProcessingException;

import domain.ImmutableGreenSourceForecastData;
import domain.job.PowerJob;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for requesting forecast data from monitoring agent
 */
public class RequestForecastData extends OneShotBehaviour {

	private static final Logger logger = LoggerFactory.getLogger(RequestForecastData.class);

	private final GreenEnergyAgent myGreenEnergyAgent;

	private final String conversationId;
	private final PowerJob powerJob;
	private final String protocol;

	/**
	 * Behaviour constructor.
	 *
	 * @param greenEnergyAgent agent which is executing the behaviour
	 * @param conversationId   conversation identifier for given job processing
	 * @param job              power job for which the weather is requested
	 */
	public RequestForecastData(GreenEnergyAgent greenEnergyAgent, String conversationId, String protocol,
			PowerJob job) {
		myGreenEnergyAgent = greenEnergyAgent;
		this.conversationId = conversationId;
		this.powerJob = job;
		this.protocol = protocol;
	}

	/**
	 * Method which sends the request to the Monitoring Agent asking for the weather at the given location.
	 */
	@Override
	public void action() {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(myGreenEnergyAgent.getMonitoringAgent());
		request.setConversationId(conversationId);
		request.setProtocol(protocol);
		var requestData = ImmutableGreenSourceForecastData.builder()
				.location(myGreenEnergyAgent.getLocation())
				.timetable(myGreenEnergyAgent.manage().getJobsTimetable(powerJob))
				.build();
		try {
			request.setContent(getMapper().writeValueAsString(requestData));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		displayMessageArrow(myGreenEnergyAgent, myGreenEnergyAgent.getMonitoringAgent());
		myAgent.send(request);
	}
}