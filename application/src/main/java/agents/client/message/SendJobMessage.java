package agents.client.message;

import domain.job.Job;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

import java.io.IOException;
import java.util.List;

import static mapper.JsonMapper.getMapper;

public class SendJobMessage {

    private final ACLMessage message;

    private SendJobMessage(final ACLMessage message) {
        this.message = message;
    }

    public static SendJobMessage create(final Job job, final List<AID> receiverList, final int performative) {
        final ACLMessage proposal = new ACLMessage(performative);
        try {
            proposal.setContent(getMapper().writeValueAsString(job));
        } catch (IOException e) {
            e.printStackTrace();
        }
        receiverList.forEach(proposal::addReceiver);
        return new SendJobMessage(proposal);
    }

    public ACLMessage getMessage() {
        return message;
    }
}
