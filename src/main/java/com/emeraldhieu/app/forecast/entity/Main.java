package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Getter
@Jacksonized
public class Main implements Serializable {

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
