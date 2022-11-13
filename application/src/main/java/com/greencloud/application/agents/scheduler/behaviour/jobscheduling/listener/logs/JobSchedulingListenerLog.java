package com.greencloud.application.agents.scheduler.behaviour.jobscheduling.listener.logs;

/**
 * Class contains all constants used in logging information in listener behaviours during job scheduling process
 */
public class JobSchedulingListenerLog {

	// CLIENTS JOB LISTENER LOG MESSAGES
	public static final String JOB_ALREADY_EXISTING_LOG =
			"Job with id {} was already announced in network. Its current state is {}";
	public static final String JOB_ENQUEUED_SUCCESSFULLY_LOG = "Job {} has been successfully added to job scheduling queue";
	public static final String QUEUE_THRESHOLD_EXCEEDED_LOG = "WARNING! The queue has reached the expected threshold!";

	// JOB UPDATE LISTENER
	public static final String JOB_UPDATE_RECEIVED_LOG =
			"Received update regarding job {} state. Passing the information to client.";

}