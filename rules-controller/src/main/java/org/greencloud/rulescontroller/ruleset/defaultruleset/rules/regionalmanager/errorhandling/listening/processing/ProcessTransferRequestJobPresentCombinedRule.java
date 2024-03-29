package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.regionalmanager.errorhandling.listening.processing;

import static org.greencloud.commons.constants.FactTypeConstants.AGENTS;
import static org.greencloud.commons.constants.FactTypeConstants.EVENT_TIME;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.enums.rules.RuleType.LISTEN_FOR_JOB_TRANSFER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_RULE;
import static org.greencloud.rulescontroller.rule.combined.domain.AgentCombinedRuleType.EXECUTE_FIRST;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.greencloud.commons.args.agent.regionalmanager.agent.RegionalManagerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.transfer.JobPowerShortageTransfer;
import org.greencloud.gui.agents.regionalmanager.RegionalManagerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentRule;
import org.greencloud.rulescontroller.rule.combined.AgentCombinedRule;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class ProcessTransferRequestJobPresentCombinedRule
		extends AgentCombinedRule<RegionalManagerAgentProps, RegionalManagerNode> {

	public ProcessTransferRequestJobPresentCombinedRule(
			final RulesController<RegionalManagerAgentProps, RegionalManagerNode> rulesController) {
		super(rulesController, EXECUTE_FIRST);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(LISTEN_FOR_JOB_TRANSFER_HANDLER_RULE,
				LISTEN_FOR_JOB_TRANSFER_HANDLE_TRANSFER_RULE,
				"transfer job handler - transfer job",
				"handles request to transfer job from one Server to another");
	}

	@Override
	protected List<AgentRule> constructRules() {
		return List.of(
				new ProcessTransferRequestNoAgentsRule(controller),
				new ProcessTransferRequestSendTransferRule(controller)
		);
	}

	@Override
	public boolean evaluateRule(final RuleSetFacts facts) {
		final Optional<ClientJob> jobOpt = facts.get(JOB);

		if (jobOpt.isPresent()) {
			facts.put(JOB, jobOpt.get());
			return true;
		}
		return false;
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final JobPowerShortageTransfer transferData = facts.get(MESSAGE_CONTENT);
		final ACLMessage message = facts.get(MESSAGE);
		final List<AID> remainingServers =
				agentProps.getRemainingAgents(message.getSender(), agentProps.getOwnedActiveServers());
		final Instant shortageStartTime = transferData.getPowerShortageStart();

		facts.put(AGENTS, remainingServers);
		facts.put(EVENT_TIME, shortageStartTime);
	}
}
