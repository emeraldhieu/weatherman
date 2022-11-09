package com.emeraldhieu.app.forecast.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
@EqualsAndHashCode
public class Weather {
    private final String id;
    private final String main;
    private final String description;
    private final String icon;
}
