package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.regionalmanager.errorhandling.weatherdrop;

import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_TYPE;
import static org.greencloud.commons.enums.rules.RuleSetType.WEATHER_DROP_RULE_SET;
import static org.greencloud.commons.enums.rules.RuleType.REQUEST_RULE_SET_UPDATE_RULE;

import java.time.Instant;
import java.util.Date;

import org.greencloud.commons.args.agent.regionalmanager.agent.RegionalManagerAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.gui.agents.regionalmanager.RegionalManagerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.behaviour.initiate.InitiateRequest;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.template.AgentScheduledRule;

public class ScheduleWeatherDropAdaptation extends AgentScheduledRule<RegionalManagerAgentProps, RegionalManagerNode> {

	public ScheduleWeatherDropAdaptation(
			final RulesController<RegionalManagerAgentProps, RegionalManagerNode> controller) {
		super(controller);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription("ADAPT_TO_WEATHER_DROP_RULE",
				"adapt the rule set upon weather drop",
				"method sends information to Servers to adapt their behaviour to the weather drop");
	}

	@Override
	protected Date specifyTime(final RuleSetFacts facts) {
		final Instant weatherDropTime = agentProps.getAgentKnowledge().get("WEATHER_DROP_START");
		return Date.from(weatherDropTime);
	}

	@Override
	protected void handleActionTrigger(final RuleSetFacts facts) {
		final RuleSetFacts handlerFacts = new RuleSetFacts(facts.get(RULE_SET_IDX));
		handlerFacts.put(RULE_SET_TYPE, WEATHER_DROP_RULE_SET);
		agent.addBehaviour(InitiateRequest.create(agent, handlerFacts, REQUEST_RULE_SET_UPDATE_RULE, controller));
	}
}
