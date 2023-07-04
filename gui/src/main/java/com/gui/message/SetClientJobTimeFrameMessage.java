package com.gui.message;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gui.message.domain.JobTimeFrame;
import com.gui.message.domain.Message;

@JsonSerialize(as = ImmutableSetClientJobTimeFrameMessage.class)
@JsonDeserialize(as = ImmutableSetClientJobTimeFrameMessage.class)
@Value.Immutable
public interface SetClientJobTimeFrameMessage extends Message {

	JobTimeFrame getData();

	String getAgentName();

	default String getType() {
		return "SET_CLIENT_JOB_TIME_FRAME";
	}
}
