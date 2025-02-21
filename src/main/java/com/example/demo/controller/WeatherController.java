package com.example.demo.controller;

import com.example.demo.entity.WeatherData;
import com.example.demo.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/weather")
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/{city}")
    public WeatherData getWeather(@PathVariable String city) {
        return weatherService.getWeather(city);
    }
}
