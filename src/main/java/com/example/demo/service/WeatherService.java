package com.example.demo.service;

import com.example.demo.entity.WeatherData;
import com.example.demo.handler.WeatherServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.api.url}")
    private String apiUrl;
    @Value("${weather.polling.enabled}")
    private boolean pollingMode;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, CachedWeather> cache = new ConcurrentHashMap<>();

    /**
     * Retrieves weather data. If the data in the cache is up to date (less than 10 minutes), it is returned from the cache.
     * If not, a request to OpenWeather API is executed.
     */
    public WeatherData getWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            log.warn("Incorrect city name transmitted: {}", city);
            throw new WeatherServiceException("The city name cannot be empty or null");
        }

        log.info("Weather request for a city: {}", city);

        if (cache.containsKey(city) && cache.get(city).isValid()) {
            log.info("Data found in the cache for the city: {}", city);
            return cache.get(city).data();
        }

        return fetchAndCacheWeather(city);
    }

    /**
     * Executes a request to the OpenWeather API and caches the result if successful.
     */
    protected WeatherData fetchAndCacheWeather(String city) {
        String url = String.format(apiUrl, city, apiKey);
        log.info("Sending a request: {}", url);

        try {
            WeatherData data = restTemplate.getForObject(url, WeatherData.class);

            if (data != null) {
                log.info("Weather data received: {}", data);
                cacheWeather(city, data);
                return data;
            } else {
                log.warn("The API returned an empty response for the city: {}", city);
                throw new WeatherServiceException("The API returned an empty response");
            }

        } catch (HttpClientErrorException.Unauthorized e) {
            log.error("Authorization error: invalid API key");
            throw new WeatherServiceException("Authorization error: invalid API key", e);

        } catch (HttpClientErrorException.NotFound e) {
            log.error("City '{}' not found", city);
            throw new WeatherServiceException("City not found: " + city, e);

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("The number of requests to the API has been exceeded. Wait a while before requesting again");
            throw new WeatherServiceException("Number of requests to API exceeded", e);

        } catch (ResourceAccessException e) {
            log.error("Network error or timeout when connecting to OpenWeather API", e);
            throw new WeatherServiceException("Network error or timeout", e);

        } catch (UnknownHttpStatusCodeException e) {
            log.error("Received unknown HTTP code from OpenWeather API: {}", e.getRawStatusCode(), e);
            throw new WeatherServiceException("Unknown API status code", e);

        } catch (Exception e) {
            log.error("Unexpected error when requesting weather data", e);
            throw new WeatherServiceException("Error when requesting weather data", e);
        }
    }

    /**
     * Adds data to the cache, removing the oldest city if the 10-city limit is exceeded.
     */
    protected void cacheWeather(String city, WeatherData data) {
        log.info("Data caching for the city: {}", city);

        if (cache.size() >= 10) {
            String oldestCity = cache.entrySet()
                    .stream()
                    .min(Comparator.comparing(entry -> entry.getValue().timestamp()))
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (oldestCity != null) {
                cache.remove(oldestCity);
                log.info("The oldest cached city has been removed: {}", oldestCity);
            }
        }
        cache.put(city, new CachedWeather(data));
    }

    /**
     * Automatic weather update every 10 minutes if polling mode is enabled.
     */
    @Scheduled(fixedRate = 600_000)
    public void updateCachedWeather() {
        if (!pollingMode) return;

        log.info("Automatic update of cached data");
        cache.keySet().forEach(this::fetchAndCacheWeather);
    }

    /**
     * Test request method to check API performance.
     */
    public void testRequest() {
        String testCity = "London";
        log.info("Test weather request for a city: {}", testCity);

        try {
            WeatherData data = getWeather(testCity);
            log.info("Test weather data: {}", data);
        } catch (Exception e) {
            log.error("Error during test request: {}", e.getMessage());
        }
    }

    /**
     * A class representing cached data.
     */
    private record CachedWeather(WeatherData data, Instant timestamp) {
        CachedWeather(WeatherData data) {
            this(data, Instant.now());
        }

        boolean isValid() {
            return Instant.now().isBefore(timestamp.plusSeconds(600));
        }
    }
}


