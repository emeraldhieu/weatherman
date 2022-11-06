package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
public class Coordinate {

    @JsonAlias("lat")
    public final String latitude;

    @JsonAlias("lon")
    public final String longitude;
}
