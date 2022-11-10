package com.emeraldhieu.app.forecast.decoder;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

/**
 * The exception body returned from {@link com.emeraldhieu.app.forecast.ForecastClient}.
 */
@Getter
@Builder
@Jacksonized
public class ExceptionMessage {
    private final String timestamp;

    @JsonAlias("cod")
    private final int status;

    private final String error;
    private final String message;
    private final String path;
}