package org.greencloud.rulescontroller.strategy.defaultstrategy.rules.scheduler.job.polling.processing;

import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_ANNOUNCEMENT_RULE;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_POLLING_HANDLE_JOB_RULE;
import static org.greencloud.commons.enums.rules.RuleType.NEW_JOB_POLLING_RULE;
import static org.greencloud.commons.utils.time.TimeSimulation.getCurrentTime;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Objects.requireNonNull;

import java.time.Instant;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.strategy.defaultstrategy.rules.scheduler.job.announcing.domain.AnnouncingConstants;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;

import org.greencloud.commons.args.agent.scheduler.agent.SchedulerAgentProps;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.facts.StrategyFacts;

import com.gui.agents.scheduler.SchedulerNode;

public class ProcessPollNextClientJobSuccessfullyRule extends AgentBasicRule<SchedulerAgentProps, SchedulerNode> {

	public ProcessPollNextClientJobSuccessfullyRule(
			final RulesController<SchedulerAgentProps, SchedulerNode> controller) {
		super(controller, 1);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(NEW_JOB_POLLING_RULE, NEW_JOB_POLLING_HANDLE_JOB_RULE,
				"poll next job to be announced",
				"when there are jobs in the queue, Scheduler polls next job");
	}

	@Override
	public boolean evaluateRule(final StrategyFacts facts) {
		return !agentProps.getJobsToBeExecuted().isEmpty();
	}

	@Override
	public void executeRule(final StrategyFacts facts) {
		final ClientJob jobToExecute = agentProps.getJobsToBeExecuted().poll();
		agentNode.updateScheduledJobQueue(agentProps);

		final StrategyFacts announcementFacts = new StrategyFacts(facts.get(STRATEGY_IDX));
		announcementFacts.put(JOB, jobToExecute);
		announcementFacts.put(RULE_TYPE, NEW_JOB_ANNOUNCEMENT_RULE);
		putAdjustedTimeFrame(requireNonNull(jobToExecute), announcementFacts);

		controller.fire(announcementFacts);
	}

	private void putAdjustedTimeFrame(final ClientJob job, final StrategyFacts facts) {
		final long jobDuration = MILLIS.between(job.getStartTime(), job.getEndTime());
		final Instant newAdjustedStart = getCurrentTime().plusMillis(AnnouncingConstants.PROCESSING_TIME_ADJUSTMENT);
		final Instant newAdjustedEnd = newAdjustedStart.plusMillis(jobDuration);

		facts.put("job-adjusted-start", newAdjustedStart);
		facts.put("job-adjusted-end", newAdjustedEnd);
	}
}