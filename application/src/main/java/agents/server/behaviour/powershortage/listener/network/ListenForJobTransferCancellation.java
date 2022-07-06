package agents.server.behaviour.powershortage.listener.network;

import static common.GUIUtils.displayMessageArrow;
import static common.GUIUtils.updateServerState;
import static common.constant.MessageProtocolConstants.CANCELLED_TRANSFER_PROTOCOL;
import static jade.lang.acl.ACLMessage.REQUEST;
import static jade.lang.acl.MessageTemplate.*;
import static mapper.JsonMapper.getMapper;
import static messages.domain.JobStatusMessageFactory.prepareFinishMessage;

import agents.greenenergy.GreenEnergyAgent;
import agents.server.ServerAgent;
import domain.job.Job;
import domain.job.JobTransfer;
import domain.job.PowerJob;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Behaviour listens for messages coming from the cloud network agent that the job transfer is to be cancelled
 */
public class ListenForJobTransferCancellation extends CyclicBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(ListenForJobTransferCancellation.class);

    private final MessageTemplate messageTemplate;
    private final ServerAgent myServerAgent;

    /**
     * Behaviour constructor
     *
     * @param myAgent agent executing the behaviour
     */
    public ListenForJobTransferCancellation(final Agent myAgent) {
        super(myAgent);
        this.myServerAgent = (ServerAgent) myAgent;
        this.messageTemplate = and(MatchPerformative(REQUEST), and(MatchProtocol(CANCELLED_TRANSFER_PROTOCOL), MatchSender(myServerAgent.getOwnerCloudNetworkAgent())));
    }

    /**
     * Method listens for the message coming from the Cloud Network requesting the transfer cancellation. It looks for the job to be cancelled and
     * cancels its execution
     */
    @Override
    public void action() {
        final ACLMessage inform = myAgent.receive(messageTemplate);

        if (Objects.nonNull(inform)) {
            try {
                final JobTransfer jobTransfer = getMapper().readValue(inform.getContent(), JobTransfer.class);
                final Job jobToCancel = myServerAgent.getJobByIdAndStartDate(jobTransfer.getJobId(), jobTransfer.getTransferTime());
                if (Objects.nonNull(jobToCancel)) {
                    logger.info("[{}] Cancelling the job with id {}", myServerAgent.getLocalName(), jobToCancel.getJobId());
                    informGreenSourceAboutJobFinish(jobToCancel, Collections.singletonList(myServerAgent.getGreenSourceForJobMap().get(jobToCancel.getJobId())));
                    myServerAgent.getServerJobs().remove(jobToCancel);
                    myServerAgent.getGreenSourceForJobMap().remove(jobToCancel.getJobId());
                    updateServerState(myServerAgent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

    private void informGreenSourceAboutJobFinish(final Job job, final List<AID> receivers) {
        final ACLMessage finishJobMessage = prepareFinishMessage(job.getJobId(), job.getStartTime(), receivers);
        displayMessageArrow(myServerAgent, receivers);
        myServerAgent.send(finishJobMessage);
    }
}
