package com.example.heatstrokealertapp;

public class HourlyWeather {
    private String time;
    private double tempC;
    private int humidity;

    public HourlyWeather(String time, double tempC, int humidity) {
        this.time = time;
        this.tempC = tempC;
        this.humidity = humidity;
    }

    public String getTime() {
        return time;
    }

    public double getTempC() {
        return tempC;
    }

    public int getHumidity() {
        return humidity;
    }
}
