package com.emeraldhieu.app.forecast;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ForecastTimeFormatter {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public LocalDateTime parse(String forecastedDateTimeStr) {
        return LocalDateTime.parse(forecastedDateTimeStr, dateTimeFormatter);
    }

    public String format(LocalDate localDate) {
        return dateFormatter.format(localDate);
    }

    public String format(LocalDateTime localDateTime) {
        return dateTimeFormatter.format(localDateTime);
    }
}
