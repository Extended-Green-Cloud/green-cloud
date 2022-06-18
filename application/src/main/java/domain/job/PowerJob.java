package domain.job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.time.OffsetDateTime;

/**
 * Object storing the data describing power request send to the green source
 */
@JsonSerialize(as = ImmutablePowerJob.class)
@JsonDeserialize(as = ImmutablePowerJob.class)
@Value.Immutable
public interface PowerJob {

    /**
     * @return unique job identifier
     */
    String getJobId();

    /**
     * @return time when the power delivery should start
     */
    OffsetDateTime getStartTime();

    /**
     * @return time when the power delivery should finish
     */
    OffsetDateTime getEndTime();

    /**
     * @return power that is to be delivered
     */
    int getPower();
}