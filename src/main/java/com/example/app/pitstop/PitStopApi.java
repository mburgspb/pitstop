package com.example.app.pitstop;

import com.example.app.pitstop.api.Incident;
import com.example.app.pitstop.api.IncidentDetails;
import com.example.app.pitstop.api.IncidentId;
import com.example.app.pitstop.api.OfferDetails;
import com.example.app.pitstop.api.OfferId;
import com.example.app.pitstop.commad.CreateIncident;
import com.example.app.user.authentication.Sender;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.IllegalCommandException;
import io.fluxcapacitor.javaclient.web.HandleGet;
import io.fluxcapacitor.javaclient.web.HandleOptions;
import io.fluxcapacitor.javaclient.web.HandlePost;
import io.fluxcapacitor.javaclient.web.Path;
import io.fluxcapacitor.javaclient.web.PathParam;
import io.fluxcapacitor.javaclient.web.WebResponse;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@Path("/api")
public class PitStopApi {

    @HandlePost("incidents")
    IncidentId reportIncident(IncidentDetails details, Sender sender) {
        var id = IncidentId.newValue();
        FluxCapacitor.sendAndForgetCommand(
                CreateIncident.builder()
                        .incidentId(id)
                        .details(details)
                        .sender(sender)
        );
        return id;
    }

    @HandleGet("incidents")
    List<Incident> getIncidents() {
        return FluxCapacitor.search(Incident.class).fetchAll();
    }

    @HandlePost("incidents/{incidentId}/offers")
    OfferId offerAssistance(@PathParam IncidentId incidentId, OfferDetails details) {
        throw new IllegalCommandException("Not implemented yet");
    }

    @HandlePost("incidents/{incidentId}/offers/{offerId}/accept")
    void acceptOffer(@PathParam IncidentId incidentId, @PathParam OfferId offerId) {
        throw new IllegalCommandException("Not implemented yet");
    }

    @HandlePost("incidents/{incidentId}/close")
    void closeIncident(@PathParam IncidentId incidentId) {
        throw new IllegalCommandException("Not implemented yet");
    }

    @Path("/api/*")
    @HandleOptions
    WebResponse corsPreflight() {
        return WebResponse.builder()
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, HEAD, TRACE")
                .header("Access-Control-Max-Age", String.valueOf(Duration.ofDays(1).toSeconds()))
                .build();
    }

}
