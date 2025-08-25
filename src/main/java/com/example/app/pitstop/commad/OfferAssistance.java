package com.example.app.pitstop.commad;

import com.example.app.pitstop.api.*;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import lombok.Builder;

import java.util.ArrayList;

@Builder
public record OfferAssistance(
        IncidentId incidentId,
        OfferId offerId,
        OfferDetails details
) implements IncidentCommand {
    @Apply
    public Incident apply(Incident incident) {
        var offers = new ArrayList<>(incident.getOffers());
        offers.add(
                Offer.builder()
                        .offerId(offerId)
                        .details(details)
                        .accepted(false)
                        .build()
        );
        return incident.withOffers(offers);
    }
}
