package com.example.app.pitstop.api;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Vehicle {
    @NotBlank String licensePlateNumber;
    String make;
    String model;
    String year;
    String color;
}
