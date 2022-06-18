package domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import domain.location.Location;
import org.immutables.value.Value;

/**
 * Object storing the data passed by the Green Source in the weather request message
 */
@JsonSerialize(as = ImmutableGreenSourceRequestData.class)
@JsonDeserialize(as = ImmutableGreenSourceRequestData.class)
@Value.Immutable
public interface GreenSourceRequestData {

    /**
     * @return location for which the weather is to be retrieved
     */
    Location getLocation();
}