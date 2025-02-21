package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.handler.WeatherServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWeather_CacheHit() {
        WeatherData mockData = new WeatherData("Moscow", List.of(new Weather("Clouds", "overcast clouds")), new Temperature(10.5, 8.0), 10000, new Wind(5.5), 123456789, new Sys(1600000000, 1600003600), 3600);
        weatherService.cacheWeather("Moscow", mockData);

        WeatherData result = weatherService.getWeather("Moscow");
        assertEquals(mockData, result);
    }


    @Test
    public void test_handles_null_city_name() {
        WeatherService weatherService = new WeatherService();

        assertThrows(WeatherServiceException.class, () -> weatherService.getWeather(null));
        assertThrows(WeatherServiceException.class, () -> weatherService.getWeather(""));
        assertThrows(WeatherServiceException.class, () -> weatherService.getWeather("   "));
    }

    @Test
    public void test_handles_special_characters_in_city_name() {
        WeatherService weatherService = spy(new WeatherService());
        WeatherData expectedData = WeatherData.builder()
                .name("New York")
                .build();
        doReturn(expectedData).when(weatherService).fetchAndCacheWeather("New York");

        WeatherData result = weatherService.getWeather("New York");

        verify(weatherService).fetchAndCacheWeather("New York");
        assertEquals(expectedData, result);
    }


    @Test
    public void test_fetches_data_when_cache_empty() {

        WeatherService weatherService = spy(new WeatherService());
        WeatherData mockData = WeatherData.builder().name("Berlin").build();
        String city = "Berlin";
        doReturn(mockData).when(weatherService).fetchAndCacheWeather(city);

        WeatherData result = weatherService.getWeather(city);

        verify(weatherService).fetchAndCacheWeather(city);
        assertEquals(mockData, result);
    }

    @Test
    public void test_fetches_new_data_when_not_cached() {
        WeatherService weatherService = spy(new WeatherService());
        WeatherData mockData = WeatherData.builder().name("Paris").build();
        String city = "Paris";
        doReturn(mockData).when(weatherService).fetchAndCacheWeather(city);

        WeatherData result = weatherService.getWeather(city);

        verify(weatherService).fetchAndCacheWeather(city);
        assertEquals(mockData, result);
    }

    @Test
    public void test_returns_cached_data_when_valid() {
        WeatherService weatherService = new WeatherService();
        WeatherData mockData = WeatherData.builder().name("London").build();
        String city = "London";
        weatherService.cacheWeather(city, mockData);

        WeatherData result = weatherService.getWeather(city);

        assertEquals(mockData, result);
    }
}
