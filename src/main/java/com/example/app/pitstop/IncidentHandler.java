package com.example.app.pitstop;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.Offer;
import com.example.app.pitstop.commad.CloseIncident;
import com.example.app.pitstop.commad.CreateIncident;
import com.example.app.pitstop.commad.IncidentCommand;
import com.example.app.pitstop.commad.OfferAssistance;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.Consumer;
import io.fluxcapacitor.javaclient.tracking.handling.HandleCommand;
import io.fluxcapacitor.javaclient.tracking.handling.HandleEvent;
import io.fluxcapacitor.javaclient.tracking.handling.HandleSchedule;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Consumer(name = "IncidentHandler", threads = 5, maxFetchSize = 1024)
public class IncidentHandler {
    @HandleEvent
    void handleEvent(CreateIncident event) {
        FluxCapacitor.scheduleCommand(
                CloseIncident.builder().incidentId(event.incidentId()).build(),
                "auto_close_incident_" + event.incidentId().toString(),
                Duration.ofHours(24)
        );
    }

    @HandleCommand
    public Incident handle(IncidentCommand command) {
        return FluxCapacitor.loadAggregate(command.incidentId(), Incident.class)
                .assertAndApply(command)
                .get();
    }

    @HandleCommand
    @HandleSchedule
    void handle(CloseIncident command) {
        FluxCapacitor.loadAggregate(command.incidentId(), Incident.class)
                .assertAndApply(command);
    }
}
