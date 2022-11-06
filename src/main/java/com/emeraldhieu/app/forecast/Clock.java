package com.emeraldhieu.app.forecast;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Used to unit-test current time.
 * See https://stackoverflow.com/questions/5622194/time-dependent-unit-tests#5622222
 */
@Component
public class Clock {

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now((ZoneOffset.UTC));
    }
}
