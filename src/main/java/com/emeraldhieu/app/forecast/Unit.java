package com.emeraldhieu.app.forecast;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum Unit {

    @JsonProperty("celsius")
    CELSIUS("celsius", "Celsius", "metric"),

    @JsonProperty("fahrenheit")
    FAHRENHEIT("fahrenheit", "Fahrenheit", "imperial");

    private final String keyword;
    private final String name;
    private final String apiUnit;


    private static final Map<String, Unit> unitsByKeyword = new HashMap<>();

    static {
        Arrays.stream(Unit.values()).forEach(unit -> {
            unitsByKeyword.put(unit.getKeyword(), unit);
        });
    }

    public static Unit forKeyword(String keyword) {
        return Optional.ofNullable(unitsByKeyword.get(keyword))
            .orElseThrow(() -> new IllegalArgumentException(String.format("Unit '%s' not found.", keyword)));
    }

    public String toKeyword() {
        return keyword;
    }
}
