package agents.cloudnetwork;

import static common.CommonUtils.getAgentsFromDF;

import agents.cloudnetwork.behaviour.CloudNetworkAgentCyclicBehaviour;
import common.GroupConstants;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.List;


public class CloudNetworkAgent extends Agent {

    private List<AID> saAgentList;

    @Override
    protected void setup() {
        super.setup();

        //TODO registration should be renewed
        registerCNAInDF();
        saAgentList = getSAAgentList(this);

        addBehaviour(CloudNetworkAgentCyclicBehaviour.createFor(this));
    }

    private List<AID> getSAAgentList(final Agent agent) {

        final DFAgentDescription template = new DFAgentDescription();
        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.SA_SERVICE_TYPE);
        serviceDescription.setOwnership(agent.getAID().getLocalName());
        template.addServices(serviceDescription);

        return getAgentsFromDF(agent, template);
    }

    private void registerCNAInDF() {

        final DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());

        final ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(GroupConstants.CNA_SERVICE_TYPE);
        serviceDescription.setName(getName());

        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);

        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
