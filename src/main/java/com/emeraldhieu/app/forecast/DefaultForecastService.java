package com.emeraldhieu.app.forecast;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultForecastService implements ForecastService {

    private final ForecastProcessor forecastProcessor;
    private final ForecastRepository forecastRepository;
    private final ForecastMapper forecastMapper;

    @Override
    public List<ForecastResponse> getCities(Unit unit, double temperature, List<String> cityIds) {
        return cityIds.stream()
            .map(cityId -> forecastRepository.getDayForecast(cityId))
            .map(forecastMapper::getForecastResponse)
            .filter(forecastResponse -> forecastProcessor.isWarm(forecastResponse.getAverageTemperature(), temperature))
            .collect(Collectors.toList());
    }

    @Override
    public List<ForecastResponse> getTemperatures(String cityId) {
        return forecastRepository.getNextDayForecasts(cityId).stream()
            .map(forecastMapper::getForecastResponse)
            .collect(Collectors.toList());
    }
}
