package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.entity.Forecast;
import com.emeraldhieu.app.forecast.entity.ForecastDataItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class to do mappings between entities and DTOs.
 */
@Component
@RequiredArgsConstructor
public class ForecastMapper {

    private final ForecastTimeFormatter forecastTimeFormatter;

    public List<CityResponse> getCityResponses(List<Forecast> forecasts) {
        return forecasts.stream()
            .map(forecast -> getCityResponse(forecast))
            .collect(Collectors.toList());
    }

    public CityResponse getCityResponse(Forecast forecast) {
        return CityResponse.builder()
            .id(forecast.getCity().getId())
            .name(forecast.getCity().getName())
            .averageTemperature(getAverageTemperature(forecast.getForecastDataItems()))
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

    public List<TemperatureResponse> getTemperatureResponses(Forecast forecast) {
        Map<LocalDate, List<ForecastDataItem>> forecastedDataItemsByDate = forecast.getForecastDataItems().stream()
            .collect(Collectors.groupingBy(forecastDataItem -> {
                    LocalDateTime forecastedTime = forecastTimeFormatter.parse(forecastDataItem.getForecastedTime());
                    return forecastedTime.toLocalDate();
                },
                LinkedHashMap::new, // Maintain insertion order
                Collectors.toList()));

        return forecastedDataItemsByDate.entrySet().stream()
            .map(entry -> {
                LocalDate date = entry.getKey();
                List<ForecastDataItem> forecastDataItems = entry.getValue();
                double averageTemperature = getAverageTemperature(forecastDataItems);
                return TemperatureResponse.builder()
                    .date(date)
                    .averageTemperature(averageTemperature)
                    .build();
            })
            .collect(Collectors.toList());
    }
}
