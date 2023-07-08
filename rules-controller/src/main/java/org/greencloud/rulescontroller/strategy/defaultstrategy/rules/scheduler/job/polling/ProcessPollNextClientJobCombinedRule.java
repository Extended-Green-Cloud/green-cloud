package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.scheduler.job.polling;

import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_POLLING_RULE;

import java.util.List;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.strategy.defaultstrategy.rules.scheduler.job.polling.processing.ProcessPollNextClientJobNoCloudAgentsRule;
import org.greencloud.rulescontroller.strategy.defaultstrategy.rules.scheduler.job.polling.processing.ProcessPollNextClientJobSuccessfullyRule;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentRule;
import org.greencloud.rulescontroller.rule.combined.AgentCombinedRule;
import org.greencloud.rulescontroller.rule.combined.domain.AgentCombinedRuleType;
import org.greencloud.rulescontroller.strategy.Strategy;

import org.greencloud.commons.args.agent.scheduler.agent.SchedulerAgentProps;

import com.gui.agents.scheduler.SchedulerNode;

public class ProcessPollNextClientJobCombinedRule extends AgentCombinedRule<SchedulerAgentProps, SchedulerNode> {

	public ProcessPollNextClientJobCombinedRule(final RulesController<SchedulerAgentProps, SchedulerNode> controller,
			final Strategy strategy) {
		super(controller, strategy, AgentCombinedRuleType.EXECUTE_FIRST);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(NEW_JOB_POLLING_RULE,
				"polling next client job from queue",
				"combined rule executed when new client job is to be polled from queue");
	}

	@Override
	protected List<AgentRule> constructRules() {
		return List.of(
				new ProcessPollNextClientJobNoCloudAgentsRule(controller),
				new ProcessPollNextClientJobSuccessfullyRule(controller)
		);
	}
}