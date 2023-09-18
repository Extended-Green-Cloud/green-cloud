package org.greencloud.rulescontroller.strategies.defaultstrategy.rules.server.job.execution;

import static org.greencloud.commons.constants.LoggingConstants.MDC_JOB_ID;
import static org.greencloud.commons.constants.LoggingConstants.MDC_STRATEGY_ID;
import static org.greencloud.commons.enums.job.JobExecutionResultEnum.STARTED;
import static org.greencloud.commons.enums.job.JobExecutionStateEnum.replaceStatusToActive;
import static org.greencloud.commons.constants.FactTypeConstants.JOB;
import static org.greencloud.commons.constants.FactTypeConstants.JOB_START_INFORM;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleType.FINISH_JOB_EXECUTION_RULE;
import static org.greencloud.commons.enums.rules.RuleType.PROCESS_START_JOB_EXECUTION_RULE;
import static org.greencloud.commons.mapper.JobMapper.mapToJobInstanceId;
import static org.greencloud.commons.utils.messaging.constants.MessageConversationConstants.BACK_UP_POWER_JOB_ID;
import static org.greencloud.commons.utils.messaging.constants.MessageConversationConstants.GREEN_POWER_JOB_ID;
import static org.greencloud.commons.utils.messaging.constants.MessageConversationConstants.ON_HOLD_JOB_ID;
import static org.greencloud.commons.utils.messaging.factory.JobStatusMessageFactory.prepareJobStartedMessage;
import static org.greencloud.commons.utils.messaging.factory.JobStatusMessageFactory.prepareJobStatusMessageForCNA;
import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.behaviour.schedule.ScheduleOnce;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.jeasy.rules.api.Facts;
import org.slf4j.Logger;
import org.slf4j.MDC;

import org.greencloud.commons.args.agent.server.agent.ServerAgentProps;
import org.greencloud.commons.domain.job.basic.ClientJob;
import org.greencloud.commons.domain.job.instance.JobInstanceIdentifier;
import org.greencloud.commons.enums.job.JobExecutionStatusEnum;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.server.ServerNode;

import jade.core.AID;

public class ProcessJobStartRule extends AgentBasicRule<ServerAgentProps, ServerNode> {

	private static final Logger logger = getLogger(ProcessJobStartRule.class);

	private ClientJob job;
	private boolean informAboutStart;

	public ProcessJobStartRule(final RulesController<ServerAgentProps, ServerNode> rulesController) {
		super(rulesController);
	}

	@Override
	public AgentRuleDescription initializeRuleDescription() {
		return new AgentRuleDescription(PROCESS_START_JOB_EXECUTION_RULE,
				"processing start job execution in Server",
				"rule handles start of Job execution in given Server");
	}

	@Override
	public void executeRule(final StrategyFacts facts) {
		job = facts.get(JOB);
		informAboutStart = facts.get(JOB_START_INFORM);

		if (!agentProps.getGreenSourceForJobMap().containsKey(job.getJobId())) {
			logger.info("Job execution couldn't start: there is no green source for the job {}",
					mapToJobInstanceId(job));
			return;
		}

		final String logMessage = informAboutStart
				? "Start executing the job {} by executing step {}."
				: "Start executing the job {} by executing step {} without informing CNA";

		MDC.put(MDC_JOB_ID, job.getJobId());
		MDC.put(MDC_STRATEGY_ID, valueOf((int) facts.get(STRATEGY_IDX)));
		logger.info(logMessage, job.getJobId());

		sendJobStartMessage(facts);
		substituteJobStatus(facts);
		agentProps.incrementJobCounter(mapToJobInstanceId(job), STARTED);

		agent.addBehaviour(
				ScheduleOnce.create(agent, facts, FINISH_JOB_EXECUTION_RULE, controller, f -> f.get(STRATEGY_IDX)));
	}

	private void sendJobStartMessage(final Facts facts) {
		final AID greenSource = agentProps.getGreenSourceForJobMap().get(job.getJobId());
		final List<AID> receivers = informAboutStart ?
				List.of(greenSource, agentProps.getOwnerCloudNetworkAgent()) :
				singletonList(greenSource);

		agent.send(prepareJobStartedMessage(job, facts.get(STRATEGY_IDX), receivers.toArray(new AID[0])));
	}

	private void substituteJobStatus(final Facts facts) {
		final JobExecutionStatusEnum currentStatus = agentProps.getServerJobs().get(job);
		final JobInstanceIdentifier jobInstance = mapToJobInstanceId(job);

		replaceStatusToActive(agentProps.getServerJobs(), job);
		agent.send(prepareJobStatusMessageForCNA(jobInstance, getStatus(currentStatus), agentProps,
				facts.get(STRATEGY_IDX)));
	}

	private String getStatus(final JobExecutionStatusEnum currentStatus) {
		return switch (currentStatus) {
			case ACCEPTED -> GREEN_POWER_JOB_ID;
			case ON_HOLD_SOURCE_SHORTAGE_PLANNED, ON_HOLD_PLANNED, ON_HOLD_TRANSFER_PLANNED -> ON_HOLD_JOB_ID;
			case IN_PROGRESS_BACKUP_ENERGY_PLANNED -> BACK_UP_POWER_JOB_ID;
			default -> null;
		};
	}
}
