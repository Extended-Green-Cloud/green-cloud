package org.greencloud.rulescontroller.strategy;

import static org.greencloud.commons.enums.strategy.StrategyType.WEATHER_DROP_STRATEGY;
import static org.greencloud.rulescontroller.rule.AgentRuleType.COMBINED;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.greencloud.commons.args.agent.AgentProps;
import org.greencloud.commons.enums.rules.RuleType;
import org.greencloud.commons.enums.strategy.StrategyType;
import org.greencloud.rulescontroller.RulesController;
import org.greencloud.rulescontroller.rule.AgentRule;
import org.greencloud.rulescontroller.rule.combined.AgentCombinedRule;
import org.greencloud.rulescontroller.strategies.defaultstrategy.DefaultStrategy;
import org.greencloud.rulescontroller.strategies.weatherdropstrategy.WeatherDropStrategy;

import com.gui.agents.AbstractNode;

/**
 * Class storing methods used to construct strategies
 */
@SuppressWarnings("unchecked")
public class StrategyConstructor {

	/**
	 * Method constructs strategy for given type
	 *
	 * @param type       type of strategy
	 * @param controller controller which runs given strategy
	 * @return Strategy
	 */
	public static <E extends AgentProps, T extends AbstractNode<?, E>> Strategy constructStrategyForType(
			final StrategyType type, final RulesController<E, T> controller) {
		return switch (type) {
			case DEFAULT_STRATEGY -> new DefaultStrategy<>(controller);
			case WEATHER_DROP_STRATEGY -> constructModifiedStrategyForType(WEATHER_DROP_STRATEGY, controller);
		};
	}

	/**
	 * Method constructs modified strategy (modifications are applied to default strategy)
	 *
	 * @param typeModifier type of strategy which modifications are to be applied
	 * @param controller   controller which runs given strategy
	 * @return Strategy
	 */
	public static <E extends AgentProps, T extends AbstractNode<?, E>> Strategy constructModifiedStrategyForType(
			final StrategyType typeModifier, final RulesController<E, T> controller) {
		final Strategy baseStrategy = new DefaultStrategy<>(controller);

		final Strategy modifications = getStrategyModification(typeModifier, controller);
		final List<RuleType> modificationsTypes = new ArrayList<>(modifications.getAgentRules().stream()
				.map(AgentRule::getRuleType)
				.toList());

		if (!modificationsTypes.isEmpty()) {
			final List<AgentRule> modifiableRules = baseStrategy.getAgentRules().stream()
					.filter(agentRule -> modificationsTypes.contains(agentRule.getRuleType()))
					.toList();

			final List<AgentRule> usedModifications =
					performModificationOfCombinedRules(modifiableRules, modifications, modificationsTypes);
			final List<AgentRule> remainingModifications = modifications.getAgentRules().stream()
					.filter(modification -> !usedModifications.contains(modification)).toList();

			baseStrategy.getAgentRules().removeIf(agentRule -> modificationsTypes.contains(agentRule.getRuleType()));
			baseStrategy.getAgentRules().addAll(remainingModifications);
		}

		return baseStrategy;
	}

	private static <E extends AgentProps, T extends AbstractNode<?, E>> Strategy getStrategyModification(
			final StrategyType typeModifier, final RulesController<E, T> controller) {
		return switch (typeModifier) {
			case WEATHER_DROP_STRATEGY -> new WeatherDropStrategy<>(controller);
			default -> throw new InvalidParameterException("Incorrect strategy type!");
		};
	}

	private static List<AgentRule> performModificationOfCombinedRules(final List<AgentRule> originalRules,
			final Strategy modifications, final List<RuleType> modificationsTypes) {
		return originalRules.stream()
				.filter(agentRule -> agentRule.getAgentRuleType().equals(COMBINED))
				.map(AgentCombinedRule.class::cast)
				.map(agentRule -> modifyCombinedRule(agentRule, modifications, modificationsTypes))
				.flatMap(Collection::stream)
				.toList();
	}

	private static <E extends AgentProps, T extends AbstractNode<?, E>> List<AgentRule> modifyCombinedRule(
			final AgentCombinedRule<E, T> combinedRule, final Strategy modifications,
			final List<RuleType> modificationsTypes) {

		final List<RuleType> subRules = combinedRule.getNestedRules();
		final List<AgentRule> applicableModifications = modifications.getAgentRules().stream()
				.filter(rule -> subRules.contains(rule.getSubRuleType()))
				.toList();
		final List<RuleType> consideredTypes = applicableModifications.stream().map(AgentRule::getSubRuleType)
				.toList();
		consideredTypes.forEach(type -> modificationsTypes.remove(combinedRule.getRuleType()));

		if (!applicableModifications.isEmpty()) {
			combinedRule.getRulesToCombine()
					.removeIf(subRule -> consideredTypes.contains(subRule.getSubRuleType()));
			combinedRule.getRulesToCombine().addAll(applicableModifications);

			return applicableModifications;
		}
		return new ArrayList<>();
	}
}
