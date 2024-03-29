package org.greencloud.rulescontroller.ruleset.defaultruleset.rules.greenenergy.job.execution;

import static java.lang.String.valueOf;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_ID;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_SET_IDX;
import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_RULE_SET_ID;
import static org.greencloud.commons.enums.job.JobExecutionResultEnum.STARTED;
import static org.greencloud.commons.enums.job.JobExecutionStateEnum.replaceStatusToActive;
import static org.greencloud.commons.enums.rules.RuleType.JOB_STATUS_RECEIVER_HANDLER_RULE;
import static org.greencloud.commons.enums.rules.RuleType.JOB_STATUS_RECEIVER_HANDLE_STARTED_JOB_RULE;
import static org.greencloud.commons.utils.messaging.constants.MessageConversationConstants.STARTED_JOB_ID;
import static org.slf4j.LoggerFactory.getLogger;

import org.greencloud.commons.args.agent.greenenergy.agent.GreenEnergyAgentProps;
import org.greencloud.commons.domain.facts.RuleSetFacts;
import org.greencloud.commons.domain.job.basic.ServerJob;
import org.greencloud.commons.domain.job.extended.JobWithStatus;
import org.greencloud.commons.domain.job.instance.JobInstanceIdentifier;
import org.greencloud.commons.enums.job.JobExecutionStatusEnum;
import org.greencloud.gui.agents.greenenergy.GreenEnergyNode;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.slf4j.Logger;
import org.slf4j.MDC;

public class ProcessPowerSupplyStartRule extends AgentBasicRule<GreenEnergyAgentProps, GreenEnergyNode> {

	private static final Logger logger = getLogger(ProcessPowerSupplyStartRule.class);

	private ServerJob job;
	private JobInstanceIdentifier jobInstance;

	public ProcessPowerSupplyStartRule(
			final RulesController<GreenEnergyAgentProps, GreenEnergyNode> rulesController) {
		super(rulesController, 2);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(JOB_STATUS_RECEIVER_HANDLER_RULE, JOB_STATUS_RECEIVER_HANDLE_STARTED_JOB_RULE,
				"handles power supply updates - started",
				"handling new updates regarding provided power supply coming from Server");
	}

	@Override
	public boolean evaluateRule(final RuleSetFacts facts) {
		job = facts.get(JOB);
		jobInstance = facts.get(JOB_ID);
		final String type = facts.get(MESSAGE_TYPE);
		return type.equals(STARTED_JOB_ID);
	}

	@Override
	public void executeRule(final RuleSetFacts facts) {
		final JobWithStatus jobWithStatus = facts.get(MESSAGE_CONTENT);

		MDC.put(MDC_JOB_ID, job.getJobId());
		MDC.put(MDC_RULE_SET_ID, valueOf((int) facts.get(RULE_SET_IDX)));
		logger.info("Started the execution of the job with id {}", jobInstance.getJobId());
		final JobExecutionStatusEnum newStatus = replaceStatusToActive(agentProps.getServerJobs(), job);

		agentProps.getJobsExecutionTime().startJobExecutionTimer(job, newStatus, jobWithStatus.getChangeTime());
		agentProps.incrementJobCounter(jobInstance, STARTED);
	}
}
