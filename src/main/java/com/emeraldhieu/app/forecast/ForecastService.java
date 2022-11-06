package com.emeraldhieu.app.forecast;

import java.util.List;

public interface ForecastService {

    List<CityResponse> getCities(Unit unit, double temperature, List<String> cityIds);

    List<TemperatureResponse> getTemperatures(String cityId);
}
