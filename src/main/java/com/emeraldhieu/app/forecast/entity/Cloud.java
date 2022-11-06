package com.emeraldhieu.app.forecast.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
public class Cloud {
    private final int all;
}
