package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.client.job.announcing;

import static org.greencloud.commons.constants.FactTypeConstants.AGENT;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_ANNOUNCEMENT_RULE;
import static org.greencloud.commons.utils.messaging.factory.JobStatusMessageFactory.prepareJobAnnouncementMessage;
import static org.slf4j.LoggerFactory.getLogger;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.slf4j.Logger;

import org.greencloud.commons.args.agent.client.agent.ClientAgentProps;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.client.ClientNode;

import jade.core.AID;

public class AnnounceNewJobToSchedulerRule extends AgentBasicRule<ClientAgentProps, ClientNode> {

	private static final Logger logger = getLogger(AnnounceNewJobToSchedulerRule.class);

	public AnnounceNewJobToSchedulerRule(final RulesController<ClientAgentProps, ClientNode> rulesController) {
		super(rulesController);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(NEW_JOB_ANNOUNCEMENT_RULE,
				"announcing new job to CNA",
				"when Scheduler Agent was found, Client announce new job");
	}

	@Override
	public void executeRule(final StrategyFacts facts) {
		final AID scheduler = facts.get(AGENT);
		logger.info("Sending job announcement information to Scheduler Agent.");
		agent.send(prepareJobAnnouncementMessage(scheduler, agentProps.getJob(), facts.get(STRATEGY_IDX)));
	}
}