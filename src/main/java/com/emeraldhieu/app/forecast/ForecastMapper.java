package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.cacheentity.DayForecast;
import com.emeraldhieu.app.forecast.entity.ForecastDataItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * A class to do mappings between entities and DTOs.
 */
@Component
@RequiredArgsConstructor
public class ForecastMapper {

    public ForecastResponse getForecastResponse(DayForecast dayForecast) {
        return ForecastResponse.builder()
            .id(dayForecast.getId())
            .name(dayForecast.getName())
            .averageTemperature(dayForecast.getAverageTemperature())
            .date(dayForecast.getDate())
            .build();
    }

    double getAverageTemperature(List<ForecastDataItem> forecastDataItems) {
        double averageTemperature = forecastDataItems.stream()
            .map(forecastDataItem -> forecastDataItem.getMain().getTemp())
            .mapToDouble(Double::doubleValue)
            .average()
            .getAsDouble();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);
        return Double.parseDouble(decimalFormat.format(averageTemperature));
    }
}
