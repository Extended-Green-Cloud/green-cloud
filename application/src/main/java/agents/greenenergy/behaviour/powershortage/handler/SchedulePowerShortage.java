package agents.greenenergy.behaviour.powershortage.handler;

import static common.TimeUtils.getCurrentTime;

import agents.greenenergy.GreenEnergyAgent;
import com.gui.domain.nodes.GreenEnergyAgentNode;
import domain.job.PowerJob;
import domain.job.PowerShortageTransfer;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Behaviour schedules when the green source should update its internal state due to the power shortage
 */
public class SchedulePowerShortage extends WakerBehaviour {

    private static final Logger logger = LoggerFactory.getLogger(SchedulePowerShortage.class);
    private final GreenEnergyAgent myGreenEnergyAgent;
    private final List<PowerJob> jobsToHalt;
    private final int newMaximumCapacity;

    /**
     * Behaviour constructor.
     *
     * @param myAgent            agent executing the behaviour
     * @param powerShortageStart time when the power shortage starts
     * @param jobsToHalt         list of the jobs to be halted
     */
    private SchedulePowerShortage(Agent myAgent, Date powerShortageStart, List<PowerJob> jobsToHalt, int newMaximumCapacity) {
        super(myAgent, powerShortageStart);
        this.myGreenEnergyAgent = (GreenEnergyAgent) myAgent;
        this.jobsToHalt = jobsToHalt;
        this.newMaximumCapacity = newMaximumCapacity;
    }

    /**
     * Method creates the behaviour based on the passed parameters
     *
     * @param powerShortageTransfer transfer that should happen at the power shortage
     * @param newMaximumCapacity    updated capacity
     * @param greenEnergyAgent      agent executing the behaviour
     * @return behaviour scheduling the power shortage handling
     */
    public static SchedulePowerShortage createFor(final PowerShortageTransfer powerShortageTransfer, final int newMaximumCapacity, final GreenEnergyAgent greenEnergyAgent) {
        final OffsetDateTime powerShortageTime = getCurrentTime().isAfter(powerShortageTransfer.getStartTime()) ? getCurrentTime() : powerShortageTransfer.getStartTime();
        return new SchedulePowerShortage(greenEnergyAgent, Date.from(powerShortageTime.toInstant()), powerShortageTransfer.getJobList(), newMaximumCapacity);
    }

    /**
     * Method creates the behaviour based on the passed parameters
     *
     * @param powerShortageTime  time when the power shortage should happen
     * @param newMaximumCapacity updated capacity
     * @param greenEnergyAgent   agent executing the behaviour
     * @return behaviour scheduling the power shortage handling
     */
    public static SchedulePowerShortage createFor(final OffsetDateTime powerShortageTime, final int newMaximumCapacity, final GreenEnergyAgent greenEnergyAgent) {
        final OffsetDateTime shortageTime = getCurrentTime().isAfter(powerShortageTime) ? getCurrentTime() : powerShortageTime;
        return new SchedulePowerShortage(greenEnergyAgent, Date.from(shortageTime.toInstant()), Collections.emptyList(), newMaximumCapacity);
    }

    /**
     * Method updates the internal state of the green source when the power shortage happens. It changes the value of the maximum
     * capacity as well as prints the information for (if there are some) jobs which will cause the power overflow that they are on hold
     */
    @Override
    protected void onWake() {
        jobsToHalt.forEach(jobToHalt -> {
            if (myGreenEnergyAgent.getPowerJobs().containsKey(jobToHalt)) {
                logger.info("[{}] Putting job {} on hold", myGreenEnergyAgent.getName(), jobToHalt.getJobId());
            }
        });
        myGreenEnergyAgent.manage().updateMaximumCapacity(newMaximumCapacity);
    }
}