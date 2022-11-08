package com.emeraldhieu.app.forecast.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Getter
@Jacksonized
public class Weather implements Serializable {
    private final String id;
    private final String main;
    private final String description;
    private final String icon;
}
