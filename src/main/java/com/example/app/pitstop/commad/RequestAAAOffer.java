package com.example.app.pitstop.commad;

import com.example.app.pitstop.api.*;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.persisting.eventsourcing.Apply;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import io.fluxcapacitor.javaclient.web.WebResponse;
import lombok.Builder;

import java.util.ArrayList;

@Builder
public record RequestAAAOffer(
        IncidentId incidentId
) implements IncidentCommand {

    private record WorkOrderDetailsRecord(
            double lat,
            double lon,
            String licensePlate,
            String driverName,
            String destinationName
    ) {}

    @Apply
    public Incident apply(Incident incident) {
        var body = new WorkOrderDetailsRecord(
                incident.getDetails().getLocation().getLatitude().doubleValue(),
                incident.getDetails().getLocation().getLongitude().doubleValue(),
                incident.getDetails().getVehicle().getLicensePlateNumber(),
                null,
                null
        );
        WebRequest request = WebRequest
                .builder()
                .url(System.getProperty("aaa.domain") + "/aaa/orders")
                .method(HttpRequestMethod.POST)
                .header("Authorization", "Token " + System.getProperty("aaa.token"))
                .payload(body)
                .build();
        WebResponse response = FluxCapacitor.get()
                .webRequestGateway().sendAndWait(request);
        response.getPayload();

        // TODO need to check what response is
        var offers = new ArrayList<>(incident.getOffers());
        offers.add(
                Offer.builder()
//                        .offerId(offerId)
//                        .details(details)
                        .accepted(false)
                        .build()
        );
        return incident.withOffers(offers);
    }
}
