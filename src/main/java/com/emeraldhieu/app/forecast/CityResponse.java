package com.emeraldhieu.app.forecast;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
@EqualsAndHashCode
public class CityResponse {
    private final String id;
    private final String name;
    private final double averageTemperature;
}
