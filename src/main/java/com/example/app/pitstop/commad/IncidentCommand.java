package com.example.app.pitstop.commad;

import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.publishing.routing.RoutingKey;
import jakarta.validation.constraints.NotNull;

public interface IncidentCommand {
    @RoutingKey
    @NotNull
    IncidentId getIncidentId();
}
