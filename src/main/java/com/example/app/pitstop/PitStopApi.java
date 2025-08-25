package com.example.app.pitstop;

import com.example.app.pitstop.api.*;
import com.example.app.pitstop.commad.*;
import com.example.app.pitstop.query.FindIncidents;
import com.example.app.refdata.api.OperatorId;
import com.example.app.refdata.api.query.GetOperators;
import com.example.app.user.api.UserId;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.tracking.handling.authentication.RequiresUser;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandleOptions;
import io.fluxcapacitor.javaclient.web.HandlePost;
import io.fluxcapacitor.javaclient.web.Path;
import io.fluxcapacitor.javaclient.web.PathParam;
import io.fluxcapacitor.javaclient.web.WebResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;

@Component
@Path("/api")
@RequiresUser
public class PitStopApi {

    @HandlePost("incidents")
    IncidentId reportIncident(IncidentDetails details, Sender user) {
        var id = IncidentId.newValue();
        FluxCapacitor.sendAndForgetCommand(
                CreateIncident.builder()
                        .incidentId(id)
                        .details(details)
                        .reporterUserId(user.getUserId())
                        .build()
        );
        return id;
    }

    @HandleGet("incidents")
    List<Incident> getIncidents() {
        Boolean closed = false;
        Integer maxResults = 100;
        var search = new FindIncidents(closed, maxResults);
        return FluxCapacitor.queryAndWait(search);
    }

    @HandlePost("incidents/{incidentId}/offers")
    OfferId offerAssistance(@PathParam IncidentId incidentId, OfferDetails details) {
        var id = OfferId.newValue();
        FluxCapacitor.sendAndForgetCommand(
                OfferAssistance.builder()
                        .offerId(id)
                        .details(details)
                        .incidentId(incidentId)
                        .build()
        );
        return id;
    }

    @HandlePost("incidents/{incidentId}/offers/aaa")
    OfferId getAAAOffer(@PathParam IncidentId incidentId) {
        var id = OfferId.newValue();
        FluxCapacitor.sendCommandAndWait(
                RequestAAAOffer.builder()
                        .incidentId(incidentId)
                        .build()
        );
        return id;
    }

    @HandlePost("incidents/{incidentId}/offers/{offerId}/accept")
    void acceptOffer(@PathParam IncidentId incidentId, @PathParam OfferId offerId) {
        FluxCapacitor.sendAndForgetCommand(
                AcceptOffer.builder()
                        .incidentId(incidentId)
                        .offerId(offerId)
                        .build()
        );
    }

    @HandlePost("incidents/{incidentId}/close")
    void closeIncident(@PathParam IncidentId incidentId) {
        FluxCapacitor.sendAndForgetCommand(
                CloseIncident.builder()
                        .incidentId(incidentId)
                        .closedAt(Instant.now())
                        .build()
        );
    }

    @Path("/api/*")
    @HandleOptions
    WebResponse corsPreflight() {
        return WebResponse.builder()
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, HEAD, TRACE")
                .header("Access-Control-Max-Age", String.valueOf(Duration.ofDays(1).toSeconds()))
                .build();
    }

    @HandlePost("incidents/generate-mock-data")
    void generateMockData() {
        int totalIncidents = 1000;
        Random random = new Random();

        var operators = FluxCapacitor.queryAndWait(new GetOperators());

        for (int i = 0; i < totalIncidents; i++) {
            // Create incident
            var incidentId = IncidentId.newValue();
            var details = IncidentDetails.builder()
                    .location(GeoLocation.builder()
                            .latitude(new BigDecimal("52.3676").add(BigDecimal.valueOf(random.nextGaussian() * 0.1)))
                            .longitude(new BigDecimal("4.9041").add(BigDecimal.valueOf(random.nextGaussian() * 0.1)))
                            .name("Location " + i)
                            .build())
                    .vehicle(Vehicle.builder()
                            .licensePlateNumber("ABC-" + String.format("%03d", i))
                            .make(random.nextBoolean() ? "Toyota" : "BMW")
                            .model("Model" + i)
                            .build())
                    .description("Test incident " + i)
                    .build();

            FluxCapacitor.sendAndForgetCommand(
                    CreateIncident.builder()
                            .incidentId(incidentId)
                            .details(details)
                            .reporterUserId(new UserId("user" + (i % 10)))
                            .build()
            );

            // Generate 0-4 offers
            int offerCount = random.nextInt(5);
            OfferId acceptedOfferId = null;

            for (int j = 0; j < offerCount; j++) {
                var offerId = OfferId.newValue();
                FluxCapacitor.sendAndForgetCommand(
                        OfferAssistance.builder()
                                .offerId(offerId)
                                .incidentId(incidentId)
                                .details(OfferDetails.builder()
                                        .operatorId(operators.get(random.nextInt(operators.size())).getOperatorId())
                                        .price(new BigDecimal(50 + random.nextInt(200)))
                                        .build())
                                .build()
                );

                // 80% chance to accept first offer
                if (j == 0 && random.nextDouble() < 0.8) {
                    acceptedOfferId = offerId;
                }
            }

            // Accept offer if we have one
            if (acceptedOfferId != null) {
                FluxCapacitor.sendAndForgetCommand(
                        AcceptOffer.builder()
                                .incidentId(incidentId)
                                .offerId(acceptedOfferId)
                                .build()
                );

                // 90% chance to close if accepted
                if (random.nextDouble() < 0.9) {
                    FluxCapacitor.sendAndForgetCommand(
                            CloseIncident.builder()
                                    .incidentId(incidentId)
                                    .closedAt(Instant.now().plus(Duration.ofDays(random.nextInt(11))))
                                    .build()
                    );
                }
            }
        }
    }
}
