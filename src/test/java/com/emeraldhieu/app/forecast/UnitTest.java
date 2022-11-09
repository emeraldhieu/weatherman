package com.emeraldhieu.app.forecast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnitTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void givenUnit_whenSerialize_thenReturnJson() throws JsonProcessingException {
        // GIVEN
        UnitTest.TestObject testObject = UnitTest.TestObject.builder()
            .unit(Unit.CELSIUS)
            .build();

        // WHEN
        String json = objectMapper.writeValueAsString(testObject);

        // THEN
        assertEquals("{\"unit\":\"celsius\"}", json);
    }

    @Test
    public void givenJson_whenDeserialize_thenReturnDay() throws JsonProcessingException {
        // GIVEN
        String json = "{\"unit\": \"celsius\"}";

        // WHEN
        UnitTest.TestObject testObject = objectMapper.readValue(json, UnitTest.TestObject.class);

        // THEN
        assertEquals(Unit.CELSIUS, testObject.getUnit());
    }

    @Test
    public void givenJson_whenDeserialize_thenThrowsInvalidFormatException() {
        // GIVEN
        String json = "{\"unit\": \"invalid\"}";

        // WHEN and THEN
        assertThrows(InvalidFormatException.class,
            () -> objectMapper.readValue(json, UnitTest.TestObject.class));
    }

    @Builder(toBuilder = true)
    @Getter
    @Jacksonized
    private static class TestObject {
        private final Unit unit;
    }
}