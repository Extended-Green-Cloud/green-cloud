package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.scheduler.adaptation;

import static com.database.knowledge.domain.action.AdaptationActionEnum.INCREASE_DEADLINE_PRIORITY;
import static org.greencloud.commons.constants.FactTypeConstants.ADAPTATION_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.RESULT;
import static org.greencloud.commons.enums.rules.RuleType.ADAPTATION_REQUEST_RULE;
import static org.greencloud.commons.utils.math.MathOperations.nextFibonacci;
import static org.slf4j.LoggerFactory.getLogger;

import org.greencloud.commons.args.agent.scheduler.agent.SchedulerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.gui.agents.scheduler.SchedulerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.slf4j.Logger;

public class IncreaseDeadlineWeightRule extends AgentBasicRule<SchedulerAgentProps, SchedulerNode> {

	private static final Logger logger = getLogger(IncreaseDeadlineWeightRule.class);

	public IncreaseDeadlineWeightRule(final RulesController<SchedulerAgentProps, SchedulerNode> controller) {
		super(controller);
	}

	@Override
	public boolean evaluateRule(final RuleSetFacts facts) {
		return facts.get(ADAPTATION_TYPE).equals(INCREASE_DEADLINE_PRIORITY);
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final int oldPriority = agentProps.getDeadlinePriority();
		agentProps.setDeadlinePriority(nextFibonacci(oldPriority));
		logger.info("Deadline weight priority increased from {} to: {}", oldPriority, agentProps.getDeadlinePriority());
		agentProps.updateGUI();
		facts.put(RESULT, true);
	}

	/**
	 * Method initialize default rule metadata
	 *
	 * @return rule description
	 */
	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(ADAPTATION_REQUEST_RULE,
				"increase weight put on deadline priority",
				"deadline priority is increased to the next Fibonacci number");
	}
}
