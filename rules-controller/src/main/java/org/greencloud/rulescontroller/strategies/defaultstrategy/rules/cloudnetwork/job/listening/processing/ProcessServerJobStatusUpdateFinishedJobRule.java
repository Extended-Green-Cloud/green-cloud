package org.greencloud.rulescontroller.strategies.defaultstrategy.rules.cloudnetwork.job.listening.processing;

import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_STRATEGY_ID;
import static org.greencloud.commons.enums.job.JobExecutionResultEnum.FINISH;
import static org.greencloud.commons.enums.job.JobExecutionStatusEnum.PROCESSING;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleType.FINISH_JOB_EXECUTION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.JOB_STATUS_RECEIVER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.JOB_STATUS_RECEIVER_HANDLE_FINISHED_JOB_RULE;
import static org.greencloud.commons.mapper.JobMapper.mapToJobInstanceId;
import static org.greencloud.commons.utils.messaging.constants.MessageConversationConstants.FINISH_JOB_ID;
import static org.greencloud.commons.utils.messaging.factory.JobStatusMessageFactory.prepareJobStatusMessageForScheduler;
import static org.greencloud.commons.utils.job.JobUtils.isJobStarted;
import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.slf4j.Logger;
import org.slf4j.MDC;

import org.greencloud.commons.args.agent.cloudnetwork.agent.CloudNetworkAgentProps;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.extended.JobWithStatus;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.cloudnetwork.CloudNetworkNode;

public class ProcessServerJobStatusUpdateFinishedJobRule extends AgentBasicRule<CloudNetworkAgentProps, CloudNetworkNode> {

	private static final Logger logger = getLogger(ProcessServerJobStatusUpdateFinishedJobRule.class);

	public ProcessServerJobStatusUpdateFinishedJobRule(final RulesController<CloudNetworkAgentProps, CloudNetworkNode> controller) {
		super(controller, 4);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(JOB_STATUS_RECEIVER_HANDLER_RULE, JOB_STATUS_RECEIVER_HANDLE_FINISHED_JOB_RULE,
				"handle finished job",
				"rule run when Server sends update regarding job finish");
	}

	@Override
	public boolean evaluateRule(final StrategyFacts facts) {
		return facts.get(MESSAGE_TYPE).equals(FINISH_JOB_ID);
	}

	@Override
	public void executeRule(final StrategyFacts facts) {
		final Optional<ClientJob> jobOptional = facts.get(JOB);

		if (jobOptional.isPresent()) {
			final ClientJob job = jobOptional.get();
			final JobWithStatus jobStatusUpdate = facts.get(MESSAGE_CONTENT);

			MDC.put(MDC_JOB_ID, job.getJobId());
			MDC.put(MDC_STRATEGY_ID, valueOf((int) facts.get(STRATEGY_IDX)));
			logger.info("Sending information that the job {} execution has finished", job.getJobId());

			if (isJobStarted(job, agentProps.getNetworkJobs())) {
				agentProps.incrementJobCounter(mapToJobInstanceId(job), FINISH);
			}
			if (!PROCESSING.equals(agentProps.getNetworkJobs().get(job))) {
				agentNode.removeActiveJob();
				agentNode.removePlannedJob();
			}
			final StrategyFacts jobRemovalFacts = new StrategyFacts(facts.get(STRATEGY_IDX));
			jobRemovalFacts.put(RULE_TYPE, FINISH_JOB_EXECUTION_RULE);
			jobRemovalFacts.put(JOB, job);
			controller.fire(jobRemovalFacts);

			agentProps.getServerForJobMap().remove(job.getJobId());
			agentProps.updateGUI();
			agent.send(prepareJobStatusMessageForScheduler(agentProps, jobStatusUpdate, FINISH_JOB_ID,
					facts.get(STRATEGY_IDX)));
		}
	}
}
