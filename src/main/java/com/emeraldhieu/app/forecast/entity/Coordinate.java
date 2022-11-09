package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Getter
@Jacksonized
@EqualsAndHashCode
public class Coordinate implements Serializable {

    @JsonAlias("lat")
    public final String latitude;

    @JsonAlias("lon")
    public final String longitude;
}
