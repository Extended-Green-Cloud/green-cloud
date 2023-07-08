package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.server.job.listening.newjob;

import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.constants.FactTypeConstants.RESOURCES;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_RECEIVER_HANDLER_RULE;
import static org.greencloud.rulescontroller.rule.combined.domain.AgentCombinedRuleType.EXECUTE_FIRST;

import java.util.List;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.strategy.defaultstrategy.rules.server.job.listening.newjob.processing.ProcessCNANewJobNoGreenSourcesRule;
import org.greencloud.rulescontroller.strategy.defaultstrategy.rules.server.job.listening.newjob.processing.ProcessCNANewJobNoResourcesRule;
import org.greencloud.rulescontroller.strategy.defaultstrategy.rules.server.job.listening.newjob.processing.ProcessCNANewJobSuccessfullyRule;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentRule;
import org.greencloud.rulescontroller.rule.combined.AgentCombinedRule;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.resources.HardwareResources;
import org.greencloud.commons.domain.facts.StrategyFacts;

import com.gui.agents.server.ServerNode;

public class ProcessCNANewJobCombinedRule extends AgentCombinedRule<ServerAgentProps, ServerNode> {

	public ProcessCNANewJobCombinedRule(final RulesController<ServerAgentProps, ServerNode> controller) {
		super(controller, EXECUTE_FIRST);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(NEW_JOB_RECEIVER_HANDLER_RULE,
				"handles new CNA job request",
				"handling new job sent by CNA");
	}

	/**
	 * Method construct set of rules that are to be combined
	 */
	@Override
	protected List<AgentRule> constructRules() {
		return List.of(
				new ProcessCNANewJobNoGreenSourcesRule(controller),
				new ProcessCNANewJobNoResourcesRule(controller),
				new ProcessCNANewJobSuccessfullyRule(controller)
		);
	}

	@Override
	public void executeRule(final StrategyFacts facts) {
		final ClientJob job = facts.get(MESSAGE_CONTENT);
		final HardwareResources resources = agentProps.getAvailableResources(job, null, null);

		facts.put(JOB, job);
		facts.put(RESOURCES, resources);
	}

}