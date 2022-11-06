package com.emeraldhieu.app.forecast;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Builder
@Getter
@Jacksonized
@EqualsAndHashCode
public class TemperatureResponse {
    private final LocalDate date;
    private final double averageTemperature;
}
