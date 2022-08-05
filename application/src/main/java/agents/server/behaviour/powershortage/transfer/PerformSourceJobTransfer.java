package agents.server.behaviour.powershortage.transfer;

import static common.GUIUtils.displayMessageArrow;
import static common.TimeUtils.getCurrentTime;
import static messages.domain.JobStatusMessageFactory.prepareJobStartedMessage;

import agents.server.ServerAgent;
import domain.job.Job;
import domain.job.JobInstanceIdentifier;
import domain.job.JobStatusEnum;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import jade.lang.acl.ACLMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Behaviour responsible for transferring a job to a new green source
 */
public class PerformSourceJobTransfer extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(PerformSourceJobTransfer.class);

    private final ServerAgent myServerAgent;
    private final JobInstanceIdentifier jobInstanceId;
    private final AID newGreenSource;

    /**
     * Behaviour constructor.
     *
     * @param myAgent       agent executing the behaviour
     * @param transferTime  time when the job transfer should happen
     * @param jobInstanceId unique identifier of the job instance
     */
    private PerformSourceJobTransfer(Agent myAgent, Date transferTime, JobInstanceIdentifier jobInstanceId, AID newGreenSource) {
        super(myAgent, transferTime);
        this.myServerAgent = (ServerAgent) myAgent;
        this.jobInstanceId = jobInstanceId;
        this.newGreenSource = newGreenSource;
    }

    /**
     * Method creates the behaviour based on the passed arguments
     *
     * @param serverAgent    server executing the behaviour
     * @param jobInstanceId  unique identifier of the job instance
     * @param newGreenSource green source which will execute the job after power shortage
     * @return behaviour which transfer the jobs between green sources
     */
    public static PerformSourceJobTransfer createFor(final ServerAgent serverAgent, final JobInstanceIdentifier jobInstanceId, final AID newGreenSource) {
        final OffsetDateTime transferTime = getCurrentTime().isAfter(jobInstanceId.getStartTime()) ? getCurrentTime() : jobInstanceId.getStartTime();
        return new PerformSourceJobTransfer(serverAgent, Date.from(transferTime.toInstant()), jobInstanceId, newGreenSource);
    }

    /**
     * Method transfers the job between green sources. It updates the internal server state.
     */
    @Override
    protected void onWake() {
        final Job jobToExecute = myServerAgent.manage().getJobByIdAndStartDate(jobInstanceId);
        if (Objects.nonNull(jobToExecute)) {
            logger.info("[{}] Updating the internal state of the server", myServerAgent.getName());
            myServerAgent.getGreenSourceForJobMap().replace(jobToExecute.getJobId(), newGreenSource);
            myServerAgent.getServerJobs().replace(jobToExecute, JobStatusEnum.IN_PROGRESS);
            startJobExecutionInNewGreenSource(jobToExecute);
            myServerAgent.removeBehaviour(this);
        }
    }

    private void startJobExecutionInNewGreenSource(final Job jobToExecute) {
        logger.info("[{}] Start executing the job in the new green source", myServerAgent.getName());
        final ACLMessage startedJobMessage = prepareJobStartedMessage(jobToExecute.getJobId(), jobToExecute.getStartTime(), List.of(newGreenSource));
        displayMessageArrow(myServerAgent, newGreenSource);
        myAgent.send(startedJobMessage);
    }
}