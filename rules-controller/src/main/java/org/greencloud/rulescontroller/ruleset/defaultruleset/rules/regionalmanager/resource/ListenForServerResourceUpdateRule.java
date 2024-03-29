package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.regionalmanager.resource;

import static org.greencloud.commons.utils.messaging.constants.MessageTemplatesConstants.LISTEN_FOR_SERVER_RESOURCE_UPDATE_TEMPLATE;

import org.greencloud.commons.args.agent.regionalmanager.agent.RegionalManagerAgentProps;
import org.greencloud.commons.domain.agent.ServerResources;
import org.greencloud.gui.agents.regionalmanager.RegionalManagerNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.template.AgentMessageListenerRule;
import org.greencloud.rulescontroller.ruleset.RuleSet;

public class ListenForServerResourceUpdateRule extends
		AgentMessageListenerRule<RegionalManagerAgentProps, RegionalManagerNode> {

	public ListenForServerResourceUpdateRule(
			final RulesController<RegionalManagerAgentProps, RegionalManagerNode> controller,
			final RuleSet ruleSet) {
		super(controller, ruleSet, ServerResources.class, LISTEN_FOR_SERVER_RESOURCE_UPDATE_TEMPLATE, 1,
				"SERVER_RESOURCE_UPDATE_HANDLER_RULE");
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription("SERVER_RESOURCE_UPDATE_RULE",
				"listen for information about update of resources in one of connected servers",
				"rule run when one of the Servers sends information about update in its resources");
	}

}
