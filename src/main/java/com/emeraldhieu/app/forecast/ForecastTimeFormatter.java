package com.emeraldhieu.app.forecast;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ForecastTimeFormatter {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("y-M-d HH:mm:ss");

    public LocalDateTime parse(String forecastedDateTimeStr) {
        return LocalDateTime.parse(forecastedDateTimeStr, dateTimeFormatter);
    }
}
