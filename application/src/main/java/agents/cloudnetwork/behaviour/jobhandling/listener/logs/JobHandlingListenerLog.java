package agents.cloudnetwork.behaviour.jobhandling.listener.logs;

/**
 * Class contains all constants used in logging information in listener behaviours during job handling process
 */
public class JobHandlingListenerLog {

	// CLIENTS JOB LISTENER LOG MESSAGES
	public static final String SEND_CFP_RETRY_LOG = "[{}] Sending call for proposal to Server Agents for a job request with jobId {}, {} retry.";
	public static final String SEND_CFP_NEW_LOG = "[{}] Sending call for proposal to Server Agents for a job request with jobId {}!";

	// JOB STATUS CHANGE LOG MESSAGES
	public static final String SEND_GREEN_POWER_STATUS_LOG = "[{}] Sending information that the job {} is executed again using green power" ;
	public static final String SEND_JOB_START_STATUS_LOG = "[{}] Sending information that the job {} execution has started" ;
	public static final String SEND_JOB_FINISH_STATUS_LOG = "[{}] Sending information that the job {} execution is finished. So far completed {} jobs!" ;
	public static final String SEND_JOB_FAILED_STATUS_LOG = "[{}] Sending information that the job {} execution has failed";
}
