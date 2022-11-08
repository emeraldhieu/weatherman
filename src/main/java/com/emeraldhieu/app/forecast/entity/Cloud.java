package com.emeraldhieu.app.forecast.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Getter
@Jacksonized
public class Cloud implements Serializable {
    private final int all;
}
