package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.server.events.resupply.processing;

import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_STRATEGY_ID;
import static org.greencloud.commons.enums.job.JobExecutionStateEnum.EXECUTING_ON_BACK_UP;
import static org.greencloud.commons.enums.job.JobExecutionStateEnum.EXECUTING_ON_GREEN;
import static org.greencloud.commons.enums.job.JobExecutionStateEnum.EXECUTING_ON_HOLD_SOURCE;
import static org.greencloud.commons.enums.job.JobExecutionStateEnum.isStatusActive;
import static org.greencloud.commons.constants.FactTypeConstants.AGENT;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleType.RESUPPLY_JOB_WITH_GREEN_POWER_RULE;
import static org.greencloud.commons.utils.messaging.constants.MessageContentConstants.JOB_NOT_FOUND_CAUSE_MESSAGE;
import static org.greencloud.commons.utils.messaging.constants.MessageConversationConstants.GREEN_POWER_JOB_ID;
import static org.greencloud.commons.utils.messaging.factory.JobStatusMessageFactory.prepareJobStatusMessageForCNA;
import static org.greencloud.commons.utils.messaging.factory.NetworkErrorMessageFactory.prepareGreenPowerSupplyRequest;
import static java.lang.String.valueOf;
import static java.util.Objects.nonNull;

import org.greencloud.commons.mapper.JobMapper;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.template.AgentRequestRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.enums.job.JobExecutionStatusEnum;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.server.ServerNode;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class ProcessJobResupplyWithGreenEnergyRule extends AgentRequestRule<ServerAgentProps, ServerNode> {

	private static final Logger logger = LoggerFactory.getLogger(ProcessJobResupplyWithGreenEnergyRule.class);

	public ProcessJobResupplyWithGreenEnergyRule(final RulesController<ServerAgentProps, ServerNode> controller) {
		super(controller);
	}

	/**
	 * Method initialize default rule metadata
	 *
	 * @return rule description
	 */
	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(RESUPPLY_JOB_WITH_GREEN_POWER_RULE,
				"initiate green energy re-supply",
				"rule initiates to re-supply a job with green energy in corresponding Green Source");
	}

	@Override
	protected ACLMessage createRequestMessage(final StrategyFacts facts) {
		final AID greenSource = facts.get(AGENT);
		return prepareGreenPowerSupplyRequest(facts.get(JOB), greenSource, facts.get(STRATEGY_IDX));
	}

	@Override
	protected void handleInform(final ACLMessage inform, final StrategyFacts facts) {
		final ClientJob job = facts.get(JOB);
		MDC.put(MDC_JOB_ID, job.getJobId());
		MDC.put(MDC_STRATEGY_ID, valueOf((int) facts.get(STRATEGY_IDX)));
		logger.info("Green source successfully supplied job {} again with green power. Changing job status in server",
				job.getJobId());

		if (agentProps.getServerJobs().containsKey(job)) {
			final JobExecutionStatusEnum jobStatus = agentProps.getServerJobs().get(job);
			final Boolean isActive = isStatusActive(jobStatus, EXECUTING_ON_HOLD_SOURCE, EXECUTING_ON_BACK_UP);

			if (nonNull(isActive)) {
				final JobExecutionStatusEnum newStatus = EXECUTING_ON_GREEN.getStatus(isActive);
				agentProps.getServerJobs().replace(job, newStatus);

				if(Boolean.TRUE.equals(isActive)) {
					agent.send(prepareJobStatusMessageForCNA(JobMapper.mapClientJobToJobInstanceId(job), GREEN_POWER_JOB_ID, agentProps,
							facts.get(STRATEGY_IDX)));
				}
			}
			agentProps.updateGUI();
		} else {
			logger.info("Job {} was not found in server", job.getJobId());
		}
	}

	@Override
	protected void handleRefuse(final ACLMessage refuse, final StrategyFacts facts) {
		final ClientJob job = facts.get(JOB);
		final String cause = refuse.getContent();

		MDC.put(MDC_JOB_ID, job.getJobId());
		MDC.put(MDC_STRATEGY_ID, valueOf((int) facts.get(STRATEGY_IDX)));
		if (nonNull(cause) && cause.equals(JOB_NOT_FOUND_CAUSE_MESSAGE)) {
			logger.info("Job {} was not found in green source", job.getJobId());
		} else {
			logger.info("Green source refused to supply job {} again with green power", job.getJobId());
		}
	}

	@Override
	protected void handleFailure(final ACLMessage failure, final StrategyFacts facts) {
		// case is omitted (it should not occur)
	}
}