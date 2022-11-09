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
public class ForecastResponse {
    private final String id;
    private final String name;
    private final double averageTemperature;
    private final LocalDate date;
}
