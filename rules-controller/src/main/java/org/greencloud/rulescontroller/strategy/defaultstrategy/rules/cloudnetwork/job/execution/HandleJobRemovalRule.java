package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.cloudnetwork.job.execution;

import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.enums.rules.RuleType.FINISH_JOB_EXECUTION_RULE;
import static org.greencloud.commons.utils.messaging.factory.StrategyAdaptationMessageFactory.prepareStrategyRemovalRequest;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;

import org.greencloud.commons.args.agent.cloudnetwork.agent.CloudNetworkAgentProps;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.cloudnetwork.CloudNetworkNode;

public class HandleJobRemovalRule extends AgentBasicRule<CloudNetworkAgentProps, CloudNetworkNode> {

	public HandleJobRemovalRule(
			final RulesController<CloudNetworkAgentProps, CloudNetworkNode> rulesController) {
		super(rulesController);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(FINISH_JOB_EXECUTION_RULE,
				"handle Job execution finish",
				"rule handles finish of job execution");
	}

	@Override
	public void executeRule(final StrategyFacts facts) {
		final ClientJob job = facts.get(JOB);
		final int strategyIdx = agentProps.removeJob(job);

		if (controller.removeStrategy(agentProps.getStrategyForJob(), strategyIdx)) {
			agent.send(prepareStrategyRemovalRequest(strategyIdx, agentProps.getOwnedServers().keySet()));
		}
	}
}