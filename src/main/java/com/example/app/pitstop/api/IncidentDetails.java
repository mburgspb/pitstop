package com.example.app.pitstop.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IncidentDetails {
    @NotNull @Valid
    GeoLocation location;
    @NotNull @Valid
    Vehicle vehicle;
    String description;
}
