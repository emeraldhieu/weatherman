package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Getter
@Jacksonized
public class City implements Serializable {

    private final String id;
    private final String name;

    @JsonAlias("coord")
    private final Coordinate coordinate;

    private final String country;
    private final int population;
    private final int timezone;
    private final String sunrise;
    private final String sunset;
}
