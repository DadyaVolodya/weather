# Weather Java SDK

## Introduction
Weather Java SDK is a simple and efficient library for accessing the OpenWeather API to retrieve weather data for a given location. The SDK is designed to be easy to use and provides built-in caching and error handling.

## Contents
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage Example](#usage-example)
- [API Endpoint](#api-endpoint)
- [Error Handling](#error-handling)
- [Contributing](#contributing)

## Installation
Add the following dependency to your `build.gradle` file:

```gradle
implementation 'com.example:weather-sdk:1.0.0'
```

## Configuration
1. Obtain an API key from [OpenWeather](https://home.openweathermap.org/api_keys).
2. Add the API key and URL to your `application.properties`:

```properties
weather.api.key=YOUR_API_KEY
weather.api.url=https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric
weather.polling.enabled=true
```

## Usage Example
```java
import com.example.weather.WeatherService;
import com.example.weather.WeatherData;

public class WeatherApp {
    public static void main(String[] args) {
        WeatherService weatherService = new WeatherService();
        WeatherData data = weatherService.getWeather("London");
        System.out.println(data);
    }
}
```

## API Endpoint
The SDK provides a RESTful API for accessing weather data:

### Get Weather by City
**Endpoint:** `GET /api/weather/{city}`

**Example Request:**
```sh
curl -X GET "http://localhost:8080/api/weather/London"
```

**Example Response:**
```json
{
  "weather": {
    "main": "Clouds",
    "description": "scattered clouds"
  },
  "temperature": {
    "temp": 12.3,
    "feels_like": 10.5
  },
  "visibility": 8000,
  "wind": {
    "speed": 4.2
  },
  "datetime": 123456789,
  "sys": {
    "sunrise": 1600000000,
    "sunset": 1600003600
  },
  "timezone": -18000,
  "name": "London"
}
```

## Error Handling
The SDK includes built-in error handling:
- If the API key is invalid, an `InvalidApiKeyException` is thrown.
- If the API request fails, a `WeatherServiceException` is thrown.

Example:
```java
try {
WeatherData data = weatherService.getWeather("InvalidCity");
} catch (WeatherServiceException e) {
        System.err.println("Error: " + e.getMessage());
        }
```

## Contributing
We welcome contributions! Please submit pull requests via GitHub.

---

This SDK simplifies OpenWeather API integration while providing caching and automatic updates in polling mode.
