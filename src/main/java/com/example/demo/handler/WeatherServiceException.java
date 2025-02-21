package com.example.demo.handler;

public class WeatherServiceException extends RuntimeException {

    /**
     * Custom exception for WeatherService error handling.
     */
    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

