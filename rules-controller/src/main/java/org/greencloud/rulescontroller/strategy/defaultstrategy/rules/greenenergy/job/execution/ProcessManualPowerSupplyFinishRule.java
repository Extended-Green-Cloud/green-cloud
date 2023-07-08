package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.greenenergy.job.execution;

import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_STRATEGY_ID;
import static org.greencloud.commons.enums.job.JobExecutionResultEnum.FINISH;
import static org.greencloud.commons.enums.job.JobExecutionStatusEnum.ACCEPTED_JOB_STATUSES;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleType.FINISH_JOB_EXECUTION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.JOB_MANUAL_FINISH_RULE;
import static org.greencloud.commons.mapper.JobMapper.mapToJobInstanceId;
import static org.greencloud.commons.utils.messaging.factory.JobStatusMessageFactory.prepareManualFinishMessageForServer;
import static org.greencloud.commons.utils.job.JobUtils.calculateExpectedJobEndTime;
import static org.greencloud.commons.utils.job.JobUtils.isJobStarted;
import static java.lang.String.valueOf;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Date;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.template.AgentScheduledRule;
import org.slf4j.Logger;
import org.slf4j.MDC;

import org.greencloud.commons.args.agent.greenenergy.agent.GreenEnergyAgentProps;
import org.greencloud.commons.domain.job.basic.ServerJob;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.greenenergy.GreenEnergyNode;

public class ProcessManualPowerSupplyFinishRule extends AgentScheduledRule<GreenEnergyAgentProps, GreenEnergyNode> {

	private static final Logger logger = getLogger(ProcessManualPowerSupplyFinishRule.class);

	public ProcessManualPowerSupplyFinishRule(
			final RulesController<GreenEnergyAgentProps, GreenEnergyNode> controller) {
		super(controller);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(JOB_MANUAL_FINISH_RULE,
				"handle manual finish of job power supply",
				"rule executes handler which completes job power supply manually when no information from Server is received");
	}

	@Override
	protected Date specifyTime(final StrategyFacts facts) {
		final ServerJob job = facts.get(JOB);
		return calculateExpectedJobEndTime(job);
	}

	@Override
	protected boolean evaluateBeforeTrigger(final StrategyFacts facts) {
		final ServerJob job = facts.get(JOB);
		final boolean isJobPresent = agentProps.getServerJobs().containsKey(job);
		return isJobPresent && ACCEPTED_JOB_STATUSES.contains(agentProps.getServerJobs().get(job));
	}

	@Override
	protected void handleActionTrigger(final StrategyFacts facts) {
		final ServerJob job = facts.get(JOB);
		MDC.put(MDC_JOB_ID, job.getJobId());
		MDC.put(MDC_STRATEGY_ID, valueOf((int) facts.get(STRATEGY_IDX)));
		logger.error("The power delivery of {} should be finished! Finishing power delivery by hand.",
				mapToJobInstanceId(job));

		if (isJobStarted(job, agentProps.getServerJobs())) {
			agentProps.incrementJobCounter(mapToJobInstanceId(job), FINISH);
		}
		final StrategyFacts factsJobEnd = new StrategyFacts(facts.get(STRATEGY_IDX));
		factsJobEnd.put(JOB, job);
		factsJobEnd.put(RULE_TYPE, FINISH_JOB_EXECUTION_RULE);
		controller.fire(factsJobEnd);

		agentProps.updateGUI();
		agent.send(
				prepareManualFinishMessageForServer(mapToJobInstanceId(job), job.getServer(), facts.get(STRATEGY_IDX)));
	}
}