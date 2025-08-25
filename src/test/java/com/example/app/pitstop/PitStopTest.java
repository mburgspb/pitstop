package com.example.app.pitstop;

import com.example.app.pitstop.api.*;
import com.example.app.pitstop.commad.CloseIncident;
import com.example.app.refdata.api.OperatorId;
import com.example.app.user.api.UserId;
import com.example.app.user.authentication.AuthenticationUtils;
import io.fluxcapacitor.javaclient.test.TestFixture;
import io.fluxcapacitor.javaclient.web.HttpRequestMethod;
import io.fluxcapacitor.javaclient.web.WebRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PitStopTest {

    final TestFixture testFixture = TestFixture.create(PitStopApi.class, IncidentHandler.class);

    @Test
    void aaaOffer() {
        testFixture.givenCommands(
                "/user/create-user.json",
                "/refdata/register-operators.json",
                "/pitstop/create-incident.json"
        )
                .withHeader("Authorization", createAuthorizationHeader("user"))
                .whenPost("/api/incidents/4/offers/aaa", null)
                .expectSuccessfulResult();
    }

    @Test
    void getViaApi() {
        testFixture.givenCommands(
                        "/user/create-user.json",
                        "/refdata/register-operators.json"
                )
                .withHeader("Authorization", createAuthorizationHeader("user"))
                .whenPost("/api/incidents", IncidentDetails.builder()
                        .vehicle(Vehicle.builder().licensePlateNumber("10-AB-CD").build())
                        .location(GeoLocation.builder().latitude(BigDecimal.TEN).longitude(BigDecimal.ONE).build())
                        .description("test")
                        .build()
                )
                .andThen()
                .whenGet("/api/incidents").<List<Incident>>expectResult(
                        l -> l.size() == 1 &&
                                l.getFirst().getOffers().isEmpty() &&
                                l.getFirst().getEnd() == null &&
                                l.getFirst().getDetails().getDescription().equalsIgnoreCase("test")
                )
                .andThen()
                .whenPost("/api/incidents/4/offers", OfferDetails.builder()
                        .price(BigDecimal.TEN)
                        .operatorId(new OperatorId("allstate"))
                        .build()
                )
                .expectSuccessfulResult()
                .andThen()
                .whenGet("/api/incidents").<List<Incident>>expectResult(
                        l -> l.getFirst().getOffers().size() == 1 &&
                                !l.getFirst().getOffers().getFirst().isAccepted()
                )
                .andThen()
                .whenPost("/api/incidents/4/offers/5/accept", null)
                .expectSuccessfulResult()
                .andThen()
                .whenGet("/api/incidents").<List<Incident>>expectResult(
                        l -> l.getFirst().getOffers().getFirst().isAccepted()
                )
                .andThen()
                .whenPost("/api/incidents/4/close", null)
                .expectSuccessfulResult()
                .andThen()
                .whenGet("/api/incidents").<List<Incident>>expectResult(
                        l -> l.getFirst().getEnd() != null
                )
        ;
    }

    String createAuthorizationHeader(String user) {
        return testFixture.getFluxCapacitor().apply(
                fc -> AuthenticationUtils.createAuthorizationHeader(new UserId(user)));
    }

    @Test
    void testCorsPreflight() {
        testFixture.whenWebRequest(WebRequest.builder().method(HttpRequestMethod.OPTIONS)
                        .url("/api/user").build())
                .expectSuccessfulResult();
    }
}