package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.server.job.listening.jobupdate.processing;

import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_STRATEGY_ID;
import static org.greencloud.commons.enums.job.JobExecutionResultEnum.FAILED;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleType.JOB_STATUS_RECEIVER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.PROCESS_TRANSFER_UPDATE_FAILURE_RULE;
import static org.greencloud.commons.utils.messaging.constants.MessageProtocolConstants.FAILED_TRANSFER_PROTOCOL;
import static org.greencloud.commons.utils.messaging.factory.NetworkErrorMessageFactory.prepareJobTransferUpdateMessageForCNA;
import static org.greencloud.commons.utils.job.JobUtils.isJobUnique;
import static jade.lang.acl.ACLMessage.INFORM;
import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.slf4j.Logger;
import org.slf4j.MDC;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.instance.JobInstanceIdentifier;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.server.ServerNode;

import jade.lang.acl.ACLMessage;

public class ProcessUpdateFromGreenSourceTransferFailureRule extends AgentBasicRule<ServerAgentProps, ServerNode> {

	private static final Logger logger = getLogger(ProcessUpdateFromGreenSourceTransferFailureRule.class);

	private ClientJob job;
	private JobInstanceIdentifier jobInstance;

	public ProcessUpdateFromGreenSourceTransferFailureRule(
			final RulesController<ServerAgentProps, ServerNode> controller) {
		super(controller, 1);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(JOB_STATUS_RECEIVER_HANDLER_RULE, PROCESS_TRANSFER_UPDATE_FAILURE_RULE,
				"handles transfer failure update",
				"handling messages received from Green Source informing about transfer failure");
	}

	@Override
	public boolean evaluateRule(final StrategyFacts facts) {
		job = facts.get(JOB);
		jobInstance = facts.get(MESSAGE_CONTENT);
		final ACLMessage message = facts.get(MESSAGE);
		return message.getPerformative() == INFORM && message.getProtocol().equals(FAILED_TRANSFER_PROTOCOL);
	}

	@Override
	public void executeRule(final StrategyFacts facts) {
		final String jobId = job.getJobId();

		if (isJobUnique(jobId, agentProps.getServerJobs())) {
			agentProps.getGreenSourceForJobMap().remove(jobId);
		}
		agentProps.removeJob(job);

		agentProps.incrementJobCounter(jobInstance, FAILED);
		agentProps.updateGUI();

		MDC.put(MDC_JOB_ID, job.getJobId());
		MDC.put(MDC_STRATEGY_ID, valueOf((int) facts.get(STRATEGY_IDX)));
		logger.info("Job {} transfer has failed in green source", jobInstance.getJobId());
		agent.send(prepareJobTransferUpdateMessageForCNA(jobInstance, FAILED_TRANSFER_PROTOCOL,
				agentProps.getOwnerCloudNetworkAgent(), facts.get(STRATEGY_IDX)));
	}
}