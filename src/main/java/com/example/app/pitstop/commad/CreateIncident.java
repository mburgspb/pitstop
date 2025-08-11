package com.example.app.pitstop.commad;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class CreateIncident implements IncidentCommand {
    IncidentId incidentId;
    IncidentDetails details;
    Sender sender;

    @Apply
    Incident apply() {
        return Incident.builder()
                .incidentId(incidentId)
                .details(details)
                .reporter(sender.getUserId())
                .start(Instant.now())
                .build();
    }
}
