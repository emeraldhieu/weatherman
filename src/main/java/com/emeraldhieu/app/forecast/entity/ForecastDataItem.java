package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder
@Getter
@Jacksonized
public class ForecastDataItem {
    private final String dt;
    private final Main main;

    @JsonAlias("weather")
    private final List<Weather> weatherItems;

    @JsonAlias("clouds")
    private final Cloud cloud;

    private final Wind wind;

    private final Rain rain;

    private final Snow snow;

    private final int visibility;
    private final double pop;
    private final Sys sys;

    @JsonAlias("dt_txt")
    private final String forecastedTime;
}
