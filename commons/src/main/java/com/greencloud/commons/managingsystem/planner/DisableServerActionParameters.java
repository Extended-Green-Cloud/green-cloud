package com.greencloud.commons.managingsystem.planner;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@JsonDeserialize(as = ImmutableDisableServerActionParameters.class)
@JsonSerialize(as = ImmutableDisableServerActionParameters.class)
public interface DisableServerActionParameters extends AdaptationActionParameters {

	@Override
	default boolean dependsOnOtherAgents() {
		return true;
	}
}
