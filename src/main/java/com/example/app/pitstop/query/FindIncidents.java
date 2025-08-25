package com.example.app.pitstop.query;

import com.example.app.pitstop.api.Incident;
import io.fluxcapacitor.javaclient.FluxCapacitor;
import io.fluxcapacitor.javaclient.tracking.handling.HandleQuery;

import java.util.List;

public record FindIncidents(Boolean closed, Integer maxResults) {
    @HandleQuery
    List<Incident> query() {
//        var stats = FluxCapacitor.search(Incident.class)
//                .lookAhead("Toyota")
//                .facetStats();

        var search = FluxCapacitor.search(Incident.class)
                .sortBy("incidentId", true);
        if (closed != null) {
            search = search.match(closed, "closed");
        }
        if (maxResults != null) {
            List<Incident> res = search.fetch(maxResults);
            return res;
        }
        List<Incident> res2 = search.fetchAll();
        return res2;
    }
}
