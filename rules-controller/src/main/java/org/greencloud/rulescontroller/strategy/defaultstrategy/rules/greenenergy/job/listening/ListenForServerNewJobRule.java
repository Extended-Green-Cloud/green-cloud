package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.greenenergy.job.listening;

import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_RECEIVER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_RECEIVER_RULE;
import static org.greencloud.commons.utils.messaging.constants.MessageTemplatesConstants.LISTEN_FOR_SERVER_NEW_JOB_TEMPLATE;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.template.AgentMessageListenerRule;
import org.greencloud.rulescontroller.strategy.Strategy;

import org.greencloud.commons.args.agent.greenenergy.agent.GreenEnergyAgentProps;
import org.greencloud.commons.domain.job.basic.EnergyJob;
import com.gui.agents.greenenergy.GreenEnergyNode;

public class ListenForServerNewJobRule extends AgentMessageListenerRule<GreenEnergyAgentProps, GreenEnergyNode> {

	public ListenForServerNewJobRule(final RulesController<GreenEnergyAgentProps, GreenEnergyNode> controller,
			final Strategy strategy) {
		super(controller, strategy, EnergyJob.class, LISTEN_FOR_SERVER_NEW_JOB_TEMPLATE, 30,
				NEW_JOB_RECEIVER_HANDLER_RULE);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(NEW_JOB_RECEIVER_RULE,
				"listen for new Server power supply request",
				"listening for new request for power supply coming from Server");
	}
}