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
public class Sys {

    @JsonAlias("pod")
    private final DayPart dayPart;
}
