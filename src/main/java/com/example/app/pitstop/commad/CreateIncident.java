package com.example.app.pitstop.commad;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.user.api.UserId;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CreateIncident(
        IncidentId incidentId,
        IncidentDetails details,
        UserId reporterUserId
) implements IncidentCommand {

    @Apply
    public Incident apply() {
        return Incident.builder()
                .incidentId(incidentId)
                .test(Long.valueOf(incidentId.getId()))
                .details(details)
                .reporter(reporterUserId)
                .startAt(Instant.now())
                .closed(false)
                .build();
    }
}
