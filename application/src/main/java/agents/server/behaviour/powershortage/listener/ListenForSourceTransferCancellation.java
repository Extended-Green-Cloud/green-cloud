package agents.server.behaviour.powershortage.listener;

import static common.GUIUtils.displayMessageArrow;
import static common.constant.MessageProtocolConstants.CANCELLED_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.INFORM;
import static jade.lang.acl.MessageTemplate.MatchPerformative;
import static jade.lang.acl.MessageTemplate.MatchProtocol;
import static jade.lang.acl.MessageTemplate.MatchSender;
import static jade.lang.acl.MessageTemplate.and;
import static mapper.JsonMapper.getMapper;
import static messages.domain.PowerShortageMessageFactory.prepareJobPowerShortageInformation;

import agents.server.ServerAgent;
import domain.job.PowerShortageJob;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Behaviour is responsible for listening for the power job transfer cancellation message coming from the green source agent
 */
public class ListenForSourceTransferCancellation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForSourceTransferCancellation.class);

    private final MessageTemplate messageTemplate;
    private final ServerAgent myServerAgent;
    private final AID chosenGreenSourceForTransfer;

    /**
     * Behaviour constructor
     *
     * @param myAgent                      agent executing the behaviour
     * @param chosenGreenSourceForTransfer green source which was supposed to execute the transferred job
     * @param affectedGreenSource          green source affected by the power fluctuation
     */
    public ListenForSourceTransferCancellation(final Agent myAgent, final AID chosenGreenSourceForTransfer, final AID affectedGreenSource) {
        super(myAgent);
        this.myServerAgent = (ServerAgent) myAgent;
        this.chosenGreenSourceForTransfer = chosenGreenSourceForTransfer;
        this.messageTemplate = and(MatchPerformative(INFORM), and(MatchProtocol(CANCELLED_TRANSFER_PROTOCOL), MatchSender(affectedGreenSource)));

    }

    /**
     * Method listens for the message coming from Green Source requesting the transfer cancellation
     * It sends the cancellation information to the green source chosen for the job transfer
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);
        if (Objects.nonNull(inform)) {
            try {
                final PowerShortageJob powerShortageJob = getMapper().readValue(inform.getContent(), PowerShortageJob.class);
                logger.info("[{}] Sending the information about job {} transfer cancellation to {}",
                            myAgent.getName(), powerShortageJob.getJobInstanceId().getJobId(), chosenGreenSourceForTransfer.getLocalName());
                displayMessageArrow(myServerAgent, chosenGreenSourceForTransfer);
                myServerAgent.send(prepareJobPowerShortageInformation(powerShortageJob, chosenGreenSourceForTransfer, CANCELLED_TRANSFER_PROTOCOL));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}