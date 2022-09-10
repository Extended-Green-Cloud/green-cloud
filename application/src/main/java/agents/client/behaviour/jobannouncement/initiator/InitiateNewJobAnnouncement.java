package agents.client.behaviour.jobannouncement.initiator;

import static agents.client.behaviour.jobannouncement.initiator.logs.JobAnnouncementInitiatorLog.INVALID_CLOUD_PROPOSAL_LOG;
import static agents.client.behaviour.jobannouncement.initiator.logs.JobAnnouncementInitiatorLog.NO_CLOUD_AVAILABLE_NO_RETRY_LOG;
import static agents.client.behaviour.jobannouncement.initiator.logs.JobAnnouncementInitiatorLog.NO_CLOUD_AVAILABLE_RETRY_LOG;
import static agents.client.behaviour.jobannouncement.initiator.logs.JobAnnouncementInitiatorLog.NO_CLOUD_RESPONSES_LOG;
import static agents.client.behaviour.jobannouncement.initiator.logs.JobAnnouncementInitiatorLog.SEND_ACCEPT_TO_CLOUD_LOG;
import static agents.client.behaviour.jobannouncement.initiator.logs.JobAnnouncementInitiatorLog.SEND_CFP_TO_CLOUD_LOG;
import static agents.client.domain.ClientAgentConstants.CLOUD_NETWORK_AGENTS;
import static agents.client.domain.ClientAgentConstants.MAX_RETRIES;
import static agents.client.domain.ClientAgentConstants.MAX_TRAFFIC_DIFFERENCE;
import static agents.client.domain.ClientAgentConstants.RETRY_PAUSE_MILLISECONDS;
import static jade.lang.acl.ACLMessage.ACCEPT_PROPOSAL;
import static messages.MessagingUtils.readMessageContent;
import static messages.MessagingUtils.rejectJobOffers;
import static messages.MessagingUtils.retrieveProposals;
import static messages.MessagingUtils.retrieveValidMessages;
import static messages.domain.constants.MessageProtocolConstants.CLIENT_JOB_CFP_PROTOCOL;
import static messages.domain.factory.CallForProposalMessageFactory.createCallForProposal;
import static messages.domain.factory.ReplyMessageFactory.prepareStringReply;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gui.agents.ClientAgentNode;
import com.gui.agents.domain.JobStatusEnum;

import agents.client.ClientAgent;
import agents.client.behaviour.jobannouncement.handler.HandleClientJobRequestRetry;
import domain.job.Job;
import domain.job.PricedJob;
import exception.IncorrectMessageContentException;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import messages.domain.constants.MessageContentConstants;

/**
 * Behaviour sends and handles job's call for proposal
 */
public class InitiateNewJobAnnouncement extends ContractNetInitiator {

	private static final Logger logger = LoggerFactory.getLogger(InitiateNewJobAnnouncement.class);

	private final transient Job job;
	private final ClientAgent myClientAgent;
	private final String guid;

	/**
	 * Behaviour constructor.
	 *
	 * @param agent agent executing the behaviour
	 * @param cfp   call for proposal message containing job details that will be sent to Cloud Network Agents
	 * @param job   the job that the client want to be executed
	 */
	public InitiateNewJobAnnouncement(final Agent agent, final ACLMessage cfp, final Job job) {
		super(agent, cfp);
		this.myClientAgent = (ClientAgent) agent;
		this.guid = agent.getName();
		this.job = job;
	}

	/**
	 * Method prepares the call for proposal message.
	 *
	 * @param callForProposal default call for proposal message
	 * @return vector containing the call for proposals with job characteristics sent to the Cloud Network Agents
	 */
	@Override
	protected Vector prepareCfps(final ACLMessage callForProposal) {
		logger.info(SEND_CFP_TO_CLOUD_LOG, guid, job.getJobId());
		final Vector<ACLMessage> vector = new Vector<>();
		final List<AID> cloudNetworks = (List<AID>) getParent().getDataStore().get(CLOUD_NETWORK_AGENTS);
		vector.add(createCallForProposal(job, cloudNetworks, CLIENT_JOB_CFP_PROTOCOL));
		return vector;
	}

	/**
	 * Method handles the responses retrieved from the Cloud Network Agents.
	 * It selects one Cloud Network Agent that will execute the job and rejects the remaining ones.
	 *
	 * @param responses   all retrieved Cloud Network Agents' responses
	 * @param acceptances vector containing accept proposal message that will be sent back to the chosen
	 *                    Cloud Network Agent
	 */
	@Override
	protected void handleAllResponses(final Vector responses, final Vector acceptances) {
		final List<ACLMessage> proposals = retrieveProposals(responses);

		if (responses.isEmpty()) {
			logger.info(NO_CLOUD_RESPONSES_LOG, guid);
			myAgent.doDelete();
		} else if (proposals.isEmpty()) {
			handleRetryProcess();
		} else {
			List<ACLMessage> validProposals = retrieveValidMessages(proposals, PricedJob.class);

			if (!validProposals.isEmpty()) {
				final ACLMessage chosenOffer = chooseCNAToExecuteJob(validProposals);
				final PricedJob pricedJob = readMessageContent(chosenOffer, PricedJob.class);
				logger.info(SEND_ACCEPT_TO_CLOUD_LOG, guid, chosenOffer.getSender().getName());

				myClientAgent.setChosenCloudNetworkAgent(chosenOffer.getSender());
				acceptances.add(prepareStringReply(chosenOffer.createReply(), pricedJob.getJobId(), ACCEPT_PROPOSAL));
				rejectJobOffers(myClientAgent, pricedJob.getJobId(), chosenOffer, proposals);
			} else {
				handleInvalidProposals(proposals);
			}
		}
	}

	private void handleInvalidProposals(final List<ACLMessage> proposals) {
		logger.info(INVALID_CLOUD_PROPOSAL_LOG, guid);
		myClientAgent.getGuiController().updateClientsCountByValue(-1);
		((ClientAgentNode) myClientAgent.getAgentNode()).updateJobStatus(JobStatusEnum.REJECTED);
		rejectJobOffers(myClientAgent, MessageContentConstants.INVALID_JOB_ID_MESSAGE, null, proposals);
	}

	private void handleRetryProcess() {
		if (myClientAgent.getRetries() < MAX_RETRIES) {
			logger.info(NO_CLOUD_AVAILABLE_RETRY_LOG, guid, myClientAgent.getRetries());
			myClientAgent.retry();
			myClientAgent.addBehaviour(new HandleClientJobRequestRetry(myAgent, RETRY_PAUSE_MILLISECONDS, job));
		} else {
			logger.info(NO_CLOUD_AVAILABLE_NO_RETRY_LOG, guid);
			myClientAgent.getGuiController().updateClientsCountByValue(-1);
			((ClientAgentNode) myClientAgent.getAgentNode()).updateJobStatus(JobStatusEnum.REJECTED);
		}
	}

	private ACLMessage chooseCNAToExecuteJob(final List<ACLMessage> receivedOffers) {
		return receivedOffers.stream().min(this::compareCNAOffers).orElseThrow();
	}

	private int compareCNAOffers(final ACLMessage cnaOffer1, final ACLMessage cnaOffer2) {
		try {
			final PricedJob cna1 = readMessageContent(cnaOffer1, PricedJob.class);
			final PricedJob cna2 = readMessageContent(cnaOffer2, PricedJob.class);

			double powerDifference = cna1.getPowerInUse() - cna2.getPowerInUse();
			int priceDifference = (int) (cna1.getPriceForJob() - cna2.getPriceForJob());
			return MAX_TRAFFIC_DIFFERENCE.isValidIntValue((int) powerDifference) ?
					priceDifference :
					(int) powerDifference;

		} catch (IncorrectMessageContentException e) {
			return Integer.MAX_VALUE;
		}
	}
}