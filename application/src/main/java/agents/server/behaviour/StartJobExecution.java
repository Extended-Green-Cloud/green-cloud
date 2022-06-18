package agents.server.behaviour;

import static messages.domain.JobStatusMessageFactory.prepareJobStartedMessage;

import agents.server.ServerAgent;
import domain.job.Job;
import domain.job.JobStatusEnum;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class StartJobExecution extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(StartJobExecution.class);
    private final ServerAgent myServerAgent;
    private final Job jobToExecute;

    /**
     * Behaviour constructor.
     *
     * @param agent   agent that is executing the behaviour
     * @param timeOut time after which the job will be executed
     * @param job     job that is to be executed
     */
    private StartJobExecution(Agent agent, long timeOut, final Job job) {
        super(agent, timeOut);
        this.jobToExecute = job;
        myServerAgent = (ServerAgent) agent;
    }

    /**
     * Method which is responsible for creating the behaviour. It calculates the time after which
     * the job execution will start. For testing purposes 1h = 2s. If the provided time is later than
     * the current time then the job execution will start immediately
     *
     * @param serverAgent  agent that will execute the behaviour
     * @param jobToExecute job that will be executed
     * @return behaviour to be run
     */
    public static StartJobExecution createFor(final ServerAgent serverAgent, final Job jobToExecute) {
        final long hourDifference = ChronoUnit.HOURS.between(OffsetDateTime.now(), jobToExecute.getStartTime());
        final long timeOut = hourDifference < 0 ? 0 : hourDifference * 2 * 1000;
        return new StartJobExecution(serverAgent, timeOut, jobToExecute);
    }

    /**
     * Method starts the execution of the job. It updates the server state, then sends the information that the execution has started to the
     * Green Source Agent and the Cloud Network. Finally, it starts the behaviour responsible for informing about job
     * execution finish.
     */
    @Override
    protected void onWake() {
        logger.info("[{}] Start executing the job for {}", myAgent.getName(), jobToExecute.getClientIdentifier());
        myServerAgent.getServerJobs().replace(jobToExecute, JobStatusEnum.IN_PROGRESS);
        final ACLMessage startedJobMessage = prepareJobStartedMessage(jobToExecute.getJobId(),
                                                                      List.of(myServerAgent.getGreenSourceForJobMap().get(jobToExecute.getJobId()),
                                                                              myServerAgent.getOwnerCloudNetworkAgent()));
        myAgent.send(startedJobMessage);
        myAgent.addBehaviour(FinishJobExecution.createFor(myServerAgent, jobToExecute));
    }
}