package com.example.heatstrokealertapp;

public class WeatherItem {
    private String date;
    private double tempMin;
    private double tempMax;
    private int humidity;
    private String icon;

    public WeatherItem(String date, double tempMin, double tempMax, int humidity, String icon) {
        this.date = date;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidity = humidity;
        this.icon = icon;
    }



    // Getters
    public String getDate() {
        return date;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getIcon() {
        return icon;
    }
}
