package com.example.app.pitstop.api;

import com.example.app.user.api.UserId;
import io.fluxcapacitor.common.search.Facet;
import io.fluxcapacitor.common.search.Sortable;
import io.fluxcapacitor.javaclient.modeling.Aggregate;
import io.fluxcapacitor.javaclient.modeling.Member;
import io.fluxcapacitor.javaclient.persisting.search.Searchable;
import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Aggregate(searchable = true)
@Builder(toBuilder = true)
@Value
@Searchable
public class Incident {
    IncidentId incidentId;
    Long test;

    IncidentDetails details;

    UserId reporter;

    Instant startAt, end;

    @Facet
    boolean closed;

    @With
    @Member
    @Builder.Default
    List<Offer> offers = new ArrayList<>();

    Assistance assistance;

    @SuppressWarnings("unused")
    public Optional<Offer> getAcceptedOffer() {
        return offers.stream().filter(Offer::isAccepted).findFirst();
    }
}
