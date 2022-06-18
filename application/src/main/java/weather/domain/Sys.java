package weather.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.jetbrains.annotations.Nullable;

@JsonSerialize(as = ImmutableSys.class)
@JsonDeserialize(as = ImmutableSys.class)
@Immutable
public interface Sys {

    @Nullable
    String getCountry();

    String getSunrise();

    String getSunset();
}