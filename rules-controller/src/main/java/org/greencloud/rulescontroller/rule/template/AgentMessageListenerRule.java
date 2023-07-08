package org.greencloud.rulescontroller.rule.template;

import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGES;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_CONTENT;
import static org.greencloud.commons.constants.FactTypeConstants.MESSAGE_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_STEP;
import static org.greencloud.commons.constants.FactTypeConstants.RULE_TYPE;
import static org.greencloud.commons.constants.FactTypeConstants.STRATEGY_IDX;
import static org.greencloud.commons.enums.rules.RuleStepType.MESSAGE_READER_PROCESS_CONTENT_STEP;
import static org.greencloud.commons.enums.rules.RuleStepType.MESSAGE_READER_READ_CONTENT_STEP;
import static org.greencloud.commons.enums.rules.RuleStepType.MESSAGE_READER_READ_STEP;
import static org.greencloud.commons.utils.messaging.MessageReader.readMessageContent;
import static org.greencloud.commons.mapper.FactsMapper.mapToStrategyFacts;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.greencloud.rulescontroller.rule.AgentRuleType.LISTENER;

import java.util.List;

import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.domain.AgentRuleDescription;
import org.greencloud.rulescontroller.rule.AgentBasicRule;
import org.greencloud.rulescontroller.rule.AgentRule;
import org.greencloud.rulescontroller.rule.AgentRuleType;
import org.greencloud.rulescontroller.rule.simple.AgentChainRule;
import org.greencloud.rulescontroller.strategy.Strategy;
import org.jeasy.rules.api.Facts;

import org.greencloud.commons.args.agent.AgentProps;
import org.greencloud.commons.enums.rules.RuleType;
import org.greencloud.commons.domain.facts.StrategyFacts;
import com.gui.agents.AbstractNode;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.Getter;

/**
 * Abstract class defining structure of a rule which handles default message retrieval behaviour
 */
@Getter
public abstract class AgentMessageListenerRule<T extends AgentProps, E extends AbstractNode<?, T>>
		extends AgentBasicRule<T, E> {

	final Strategy strategy;
	final Class<?> contentType;
	final MessageTemplate messageTemplate;
	final int batchSize;
	final RuleType handlerRuleType;

	/**
	 * Constructor
	 *
	 * @param controller      rules controller connected to the agent
	 * @param strategy        currently executed strategy
	 * @param contentType     type of content read in the messages
	 * @param template        template used to read messages
	 * @param batchSize       number of messages read at once
	 * @param handlerRuleType rule run when the messages are present
	 */
	protected AgentMessageListenerRule(final RulesController<T, E> controller,
			final Strategy strategy, final Class<?> contentType, final MessageTemplate template, final int batchSize,
			final RuleType handlerRuleType) {
		super(controller);
		this.contentType = contentType;
		this.messageTemplate = template;
		this.strategy = strategy;
		this.batchSize = batchSize;
		this.handlerRuleType = handlerRuleType;
	}

	/**
	 * Constructor
	 *
	 * @param controller      rules controller connected to the agent
	 * @param strategy        currently executed strategy
	 * @param template        template used to read messages
	 * @param batchSize       number of messages read at once
	 * @param handlerRuleType rule run when the messages are present
	 */
	protected AgentMessageListenerRule(final RulesController<T, E> controller, final Strategy strategy,
			final MessageTemplate template, final int batchSize, final RuleType handlerRuleType) {
		super(controller);
		this.contentType = null;
		this.messageTemplate = template;
		this.strategy = strategy;
		this.batchSize = batchSize;
		this.handlerRuleType = handlerRuleType;
	}

	@Override
	public AgentRuleType getAgentRuleType() {
		return LISTENER;
	}

	/**
	 * Method returns list of nested rules
	 *
	 * @return nested rules
	 */
	@Override
	public List<AgentRule> getRules() {
		return List.of(new ReadMessagesRule(), new ReadMessagesContentRule(), new HandleMessageRule());
	}

	/**
	 * Method can be optionally overwritten in order to change strategy based on facts after reading message content
	 */
	protected int selectStrategyIdx(final StrategyFacts facts) {
		return facts.get(STRATEGY_IDX);
	}

	class ReadMessagesRule extends AgentBasicRule<T, E> {

		public ReadMessagesRule() {
			super(AgentMessageListenerRule.this.controller);
			this.isRuleStep = true;
		}

		@Override
		public void executeRule(final StrategyFacts facts) {
			final List<ACLMessage> messages = agent.receive(messageTemplate, batchSize);
			facts.put(MESSAGES, ofNullable(messages).orElse(emptyList()));
		}

		@Override
		public AgentRuleDescription initializeRuleDescription() {
			return new AgentRuleDescription(AgentMessageListenerRule.this.ruleType, MESSAGE_READER_READ_STEP,
					format("%s - read messages", AgentMessageListenerRule.this.name),
					"when new message event is triggerred, agent attempts to read messages corresponding to"
							+ "selected template");
		}
	}

	class ReadMessagesContentRule extends AgentChainRule<T, E> {

		public ReadMessagesContentRule() {
			super(AgentMessageListenerRule.this.controller, AgentMessageListenerRule.this.strategy);
			this.isRuleStep = true;
		}

		@Override
		public void executeRule(final StrategyFacts facts) {
			final ACLMessage message = facts.get(MESSAGE);

			if (nonNull(contentType)) {
				final Object content = readMessageContent(message, contentType);
				facts.put(MESSAGE_CONTENT, content);
			}
			final int strategyIdx = selectStrategyIdx(facts);

			facts.put(STRATEGY_IDX, strategyIdx);
			facts.put(MESSAGE_TYPE, ofNullable(message.getConversationId()).orElse(""));
			facts.put(RULE_STEP, MESSAGE_READER_PROCESS_CONTENT_STEP);
		}

		@Override
		public AgentRuleDescription initializeRuleDescription() {
			return new AgentRuleDescription(AgentMessageListenerRule.this.ruleType,
					MESSAGE_READER_READ_CONTENT_STEP,
					format("%s - read message content", AgentMessageListenerRule.this.name),
					"when new message matching given template is present, then agent reads its content");
		}
	}

	class HandleMessageRule extends AgentBasicRule<T, E> {

		public HandleMessageRule() {
			super(AgentMessageListenerRule.this.controller);
			this.isRuleStep = true;
		}

		@Override
		public boolean evaluateRule(final StrategyFacts facts) {
			return true;
		}

		@Override
		public void execute(final Facts facts) throws Exception {
			final StrategyFacts triggerFacts = mapToStrategyFacts(facts);
			triggerFacts.put(RULE_TYPE, handlerRuleType);
			controller.fire(triggerFacts);
		}

		@Override
		public AgentRuleDescription initializeRuleDescription() {
			return new AgentRuleDescription(AgentMessageListenerRule.this.ruleType,
					MESSAGE_READER_PROCESS_CONTENT_STEP,
					format("%s - handle message", AgentMessageListenerRule.this.name),
					"when agent reads message of given type, its handler is run");
		}
	}

}