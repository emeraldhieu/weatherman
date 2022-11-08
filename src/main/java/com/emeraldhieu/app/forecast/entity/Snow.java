package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Getter
@Jacksonized
public class Snow implements Serializable {

    @JsonAlias("3h")
    private final double lastThreeHourSnowVolume;
}
