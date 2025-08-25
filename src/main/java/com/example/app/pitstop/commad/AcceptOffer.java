package com.example.app.pitstop.commad;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.OfferId;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;

@Builder
public record AcceptOffer(IncidentId incidentId, OfferId offerId) implements IncidentCommand {
    @Apply
    public Incident apply(Incident incident) {
        var updatedOffers = incident.getOffers().stream()
                .map(o -> o.getOfferId().equals(offerId) 
                    ? o.toBuilder().accepted(true).build() 
                    : o)
                .toList();
        
        return incident.withOffers(updatedOffers);
    }
}
