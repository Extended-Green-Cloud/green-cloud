package org.greencloud.rulescontroller.strategies.defaultstrategy.rules.server.events.dividejob;

import static org.greencloud.commons.enums.job.JobExecutionResultEnum.ACCEPTED;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_DIVIDED;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_FINISH_INFORM;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_START_INFORM;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleType.FINISH_JOB_EXECUTION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.PROCESS_JOB_DIVISION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.START_JOB_EXECUTION_RULE;
import static org.greencloud.commons.mapper.JobMapper.mapToJobInstanceId;
import static org.greencloud.commons.utils.job.JobUtils.isJobStarted;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.behaviour.schedule.ScheduleOnce;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.transfer.JobDivided;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.server.ServerNode;

public class ProcessJobDivisionRule extends AgentBasicRule<ServerAgentProps, ServerNode> {

	public ProcessJobDivisionRule(final RulesController<ServerAgentProps, ServerNode> rulesController) {
		super(rulesController);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(PROCESS_JOB_DIVISION_RULE,
				"divides job instances into 2",
				"rule produces 2 job instances and initiates their execution");
	}

	@Override
	public void executeRule(final StrategyFacts facts) {
		final JobDivided<ClientJob> jobInstances = facts.get(JOB_DIVIDED);
		final ClientJob affectedJob = jobInstances.getSecondInstance();
		final ClientJob nonAffectedJob = jobInstances.getFirstInstance();

		final StrategyFacts jobStartFacts = new StrategyFacts(facts.get(STRATEGY_IDX));

		jobStartFacts.put(RULE_TYPE, START_JOB_EXECUTION_RULE);
		jobStartFacts.put(JOB, affectedJob);
		jobStartFacts.put(JOB_START_INFORM, false);
		jobStartFacts.put(JOB_FINISH_INFORM, true);

		agent.addBehaviour(ScheduleOnce.create(agent, jobStartFacts, START_JOB_EXECUTION_RULE, controller,
				f -> f.get(STRATEGY_IDX)));
		agentProps.incrementJobCounter(mapToJobInstanceId(affectedJob), ACCEPTED);

		final StrategyFacts jobFinishFacts = new StrategyFacts(facts.get(STRATEGY_IDX));

		jobFinishFacts.put(RULE_TYPE, FINISH_JOB_EXECUTION_RULE);
		jobFinishFacts.put(JOB, nonAffectedJob);
		jobFinishFacts.put(JOB_FINISH_INFORM, false);

		agent.addBehaviour(ScheduleOnce.create(agent, jobFinishFacts, FINISH_JOB_EXECUTION_RULE, controller,
				f -> f.get(STRATEGY_IDX)));

		if (!isJobStarted(nonAffectedJob, agentProps.getServerJobs())) {
			final StrategyFacts jobStartFactsNonAffected = new StrategyFacts(facts.get(STRATEGY_IDX));

			jobStartFactsNonAffected.put(RULE_TYPE, START_JOB_EXECUTION_RULE);
			jobStartFactsNonAffected.put(JOB, nonAffectedJob);
			jobStartFactsNonAffected.put(JOB_START_INFORM, true);
			jobStartFactsNonAffected.put(JOB_FINISH_INFORM, false);

			agent.addBehaviour(
					ScheduleOnce.create(agent, jobStartFactsNonAffected, START_JOB_EXECUTION_RULE, controller,
							f -> f.get(STRATEGY_IDX)));
		}
	}
}
