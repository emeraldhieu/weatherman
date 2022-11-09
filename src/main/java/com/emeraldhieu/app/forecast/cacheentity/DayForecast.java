package com.emeraldhieu.app.forecast.cacheentity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@Jacksonized
@EqualsAndHashCode
public class DayForecast implements Serializable {

    private final String id;
    private final String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate date;

    private final double averageTemperature;
}
