package com.emeraldhieu.app.forecast.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Builder
@Getter
@Jacksonized
@EqualsAndHashCode
public class Wind implements Serializable {

    private final double speed;

    @JsonAlias("deg")
    private final int degree;

    private final double gust;
}
