package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DayPart {

    @JsonProperty("d")
    DAY("d", "Day"),

    @JsonProperty("n")
    NIGHT("n", "Night");

    private final String keyword;
    private final String name;
}
