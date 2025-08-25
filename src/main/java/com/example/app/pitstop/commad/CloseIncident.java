package com.example.app.pitstop.commad;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CloseIncident(IncidentId incidentId, Instant closedAt) implements IncidentCommand {
    @Apply
    public Incident apply(Incident incident) {
        return incident.toBuilder()
                .end(closedAt)
                .closed(true)
                .build();
    }
}
