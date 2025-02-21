package com.example.demo;

import com.example.demo.service.WeatherService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class WeatherApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(WeatherApplication.class, args);
        WeatherService weatherService = context.getBean(WeatherService.class);
        weatherService.testRequest();
    }
}

