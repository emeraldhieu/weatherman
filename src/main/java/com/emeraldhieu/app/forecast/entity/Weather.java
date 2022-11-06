package com.emeraldhieu.app.forecast.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
public class Weather {
    private final String id;
    private final String main;
    private final String description;
    private final String icon;
}
