package com.emeraldhieu.app.forecast;

import java.util.List;

public interface ForecastService {

    List<ForecastResponse> getCities(Unit unit, double temperature, List<String> cityIds);

    List<ForecastResponse> getTemperatures(String cityId);
}
