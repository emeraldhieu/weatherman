package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Jacksonized
@EqualsAndHashCode
public class Forecast {

    @JsonAlias("cod")
    private final String code;

    private final String message;

    @JsonAlias("cnt")
    private final int count;

    @JsonAlias("list")
    private final List<ForecastDataItem> forecastDataItems;

    private final City city;
}
