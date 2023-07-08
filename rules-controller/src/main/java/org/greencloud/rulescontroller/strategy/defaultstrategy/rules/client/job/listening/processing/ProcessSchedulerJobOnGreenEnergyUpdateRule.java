package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.client.job.listening.processing;

import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_STRATEGY_ID;
import static org.greencloud.commons.enums.job.JobClientStatusEnum.IN_PROGRESS;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleType.JOB_STATUS_RECEIVER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.JOB_STATUS_RECEIVER_HANDLE_ON_GREEN_RULE;
import static org.greencloud.commons.utils.messaging.MessageReader.readMessageContent;
import static org.greencloud.commons.utils.messaging.constants.MessageConversationConstants.GREEN_POWER_JOB_ID;
import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.slf4j.Logger;
import org.slf4j.MDC;

import org.greencloud.commons.args.agent.client.agent.ClientAgentProps;
import org.greencloud.commons.domain.job.extended.JobWithStatus;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.client.ClientNode;

import jade.lang.acl.ACLMessage;

public class ProcessSchedulerJobOnGreenEnergyUpdateRule extends AgentBasicRule<ClientAgentProps, ClientNode> {

	private static final Logger logger = getLogger(ProcessSchedulerJobOnGreenEnergyUpdateRule.class);

	private ACLMessage message;

	public ProcessSchedulerJobOnGreenEnergyUpdateRule(
			final RulesController<ClientAgentProps, ClientNode> rulesController) {
		super(rulesController, 4);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(JOB_STATUS_RECEIVER_HANDLER_RULE, JOB_STATUS_RECEIVER_HANDLE_ON_GREEN_RULE,
				"handling job status update",
				"triggers handlers upon job status updates");
	}

	@Override
	public boolean evaluateRule(final StrategyFacts facts) {
		message = facts.get(MESSAGE);
		return message.getConversationId().equals(GREEN_POWER_JOB_ID);
	}

	/**
	 * Method executes given rule
	 *
	 * @param facts facts used in evaluation
	 */
	@Override
	public void executeRule(final StrategyFacts facts) {
		final JobWithStatus jobUpdate = readMessageContent(message, JobWithStatus.class);

		MDC.put(MDC_JOB_ID, agentProps.getJob().getJobId());
		MDC.put(MDC_STRATEGY_ID, valueOf((int) facts.get(STRATEGY_IDX)));
		logger.info("My job is again being executed using the green power!");

		agentNode.measureTimeToRetrieveTheMessage(jobUpdate, agentProps);
		agentNode.updateJobStatus(IN_PROGRESS);
		agentProps.updateJobStatusDuration(IN_PROGRESS, jobUpdate.getChangeTime());
		agentProps.saveMonitoringData();
	}
}