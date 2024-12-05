package com.example.heatstrokealertapp;

public class HourlyWeather {
    private String time;
    private double tempC;
    private String iconPath;  // This will store the icon path

    // Constructor to initialize HourlyWeather object with time, tempC, humidity, and iconPath
    public HourlyWeather(String time, double tempC, String iconPath) {
        this.time = time;
        this.tempC = tempC;
        this.iconPath = iconPath;
    }

    // Getter methods
    public String getTime() {
        return time;
    }

    public double getTempC() {
        return tempC;
    }


    public String getIconPath() {
        return iconPath;  // Return the icon path
    }
}
