package com.emeraldhieu.app.forecast;

import com.emeraldhieu.app.forecast.cacheentity.DayForecast;
import com.emeraldhieu.app.forecast.entity.City;
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

@Component
@RequiredArgsConstructor
public class ForecastProcessor {

    private final Clock clock;
    private final ForecastTimeFormatter forecastTimeFormatter;
    private static final int TIMESTAMP_OF_DAY = 8; // There are 8 three-hour timestamps a day.

    /**
     * Get remaining timestamps of today plus 8 timestamps of the next day.
     * Since the external API doesn't support offset, we have to include remaining timestamps.
     */
    public int getTimestampCount() {
        int remainingTimestampsOfToday = getRemainingTimestampsOfToday();
        return remainingTimestampsOfToday + TIMESTAMP_OF_DAY;
    }

    /**
     * Get remaining timestamps of today based on current time.
     * For example, if current time is 18:00, the only remaining timestamp is 21:00.
     */
    private int getRemainingTimestampsOfToday() {
        int currentHour = clock.getCurrentLocalDateTime().getHour(); // e.g. 18
        int remainingHour = 24 - currentHour;
        return (int) Math.ceil((double) remainingHour * TIMESTAMP_OF_DAY / 24) - 1;
    }

    public boolean isWarm(double averageTemperature, double minTemperature) {
        return averageTemperature > minTemperature;
    }

    /**
     * Get valid day forecasts that begin from tomorrow
     * and sufficiently have {@link #TIMESTAMP_OF_DAY} timestamps.
     */
    public List<DayForecast> getDayForecasts(Forecast forecast) {
        Map<LocalDate, List<ForecastDataItem>> forecastedDataItemsByDate = groupForecastDataItemsByLocalDate(forecast);
        LocalDate todayLocalDate = clock.getCurrentLocalDate();
        return forecastedDataItemsByDate.entrySet().stream()
            .filter(entry -> isValidDayForecast(todayLocalDate, entry.getKey(), entry.getValue()))
            .map(entry -> incorporateUnit(forecast.getCity(), entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    DayForecast incorporateUnit(City city, LocalDate localDate, List<ForecastDataItem> forecastDataItems) {
        double averageTemperature = getAverageTemperature(forecastDataItems);
        return DayForecast.builder()
            .id(city.getId())
            .name(city.getName())
            .date(localDate)
            .averageTemperature(averageTemperature)
            .build();
    }

    boolean isValidDayForecast(LocalDate todayLocalDate, LocalDate localDate, List<ForecastDataItem> forecastDataItems) {
        if (localDate.compareTo(todayLocalDate) > 0 // Date must be future
            && forecastDataItems.size() == TIMESTAMP_OF_DAY) {  // sufficient data to calculate accurate average temperature
            return true;
        }
        return false;
    }

    private Map<LocalDate, List<ForecastDataItem>> groupForecastDataItemsByLocalDate(Forecast forecast) {
        Map<LocalDate, List<ForecastDataItem>> forecastedDataItemsByDate = forecast.getForecastDataItems().stream()
            .collect(Collectors.groupingBy(forecastDataItem -> {
                    LocalDateTime forecastedTime = forecastTimeFormatter.parse(forecastDataItem.getForecastedTime());
                    return forecastedTime.toLocalDate();
                },
                LinkedHashMap::new, // Maintain insertion order
                Collectors.toList()));
        return forecastedDataItemsByDate;
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

    public DayForecast incorporateUnit(DayForecast dayForecast, Unit unit) {
        if (unit == Unit.FAHRENHEIT) {
            double fahrenheitTemperature = convertToFahrenheit(dayForecast.getAverageTemperature());
            return dayForecast.toBuilder()
                .averageTemperature(fahrenheitTemperature)
                .build();
        }
        return dayForecast;
    }

    private double convertToFahrenheit(double temperature) {
        return ((temperature * 9) / 5) + 32;
    }
}
