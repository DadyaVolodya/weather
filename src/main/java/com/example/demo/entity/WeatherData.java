package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {
    private String name;
    private List<Weather> weather;
    private Temperature main;
    private int visibility;
    private Wind wind;
    private long dt;
    private Sys sys;
    private int timezone;


    @Override
    public String toString() {
        return "WeatherData{" +
                "weather=" + weather +
                ", main=" + main +
                ", visibility=" + visibility +
                ", wind=" + wind +
                ", dt=" + dt +
                ", sys=" + sys +
                ", timezone=" + timezone +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherData that = (WeatherData) o;
        return Objects.equals(weather, that.weather);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(weather);
    }
}
