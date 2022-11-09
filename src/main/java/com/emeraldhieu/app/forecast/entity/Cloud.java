package com.emeraldhieu.app.forecast.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Getter
@Jacksonized
@EqualsAndHashCode
public class Cloud implements Serializable {
    private final int all;
}
