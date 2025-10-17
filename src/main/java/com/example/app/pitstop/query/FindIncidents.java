package com.example.app.pitstop.query;

import com.example.app.pitstop.api.Incident;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;

import java.util.List;

public record FindIncidents(Boolean closed, Integer maxResults) {
    @HandleQuery
    List<Incident> query() {

        // details/location/longitude BigDecimal
        // closed bool
        // start
        // reporter

        // Werkt niet
        // incidentId/id
        // incidentId
        var search = FluxCapacitor.search(Incident.class)
                .sortBy("test", false);
        List<Incident> res2 = search.fetch(100, Incident.class);
        return res2;
    }
}
