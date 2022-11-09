package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
@EqualsAndHashCode
public class Main {

    private final double temp;

    @JsonAlias("feels_like")
    private final double feelsLike;

    @JsonAlias("temp_min")
    private final double tempMin;

    @JsonAlias("temp_max")
    private final double tempMax;

    private final int pressure;
    private final int seaLevel;

    @JsonAlias("grnd_level")
    private final int groundLevel;

    private final int humidity;
    private final int tempKf;
}
