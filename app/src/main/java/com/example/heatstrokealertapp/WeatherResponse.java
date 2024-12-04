package com.example.heatstrokealertapp;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    public static class Daily {
        @SerializedName("dt")
        public long dt;
        @SerializedName("temp")
        public Temp temp;
        @SerializedName("humidity")
        public int humidity;
        @SerializedName("weather")
        public Weather[] weather;

        public static class Temp {
            @SerializedName("min")
            public double min;
            @SerializedName("max")
            public double max;
        }

        public static class Weather {
            @SerializedName("icon")
            public String icon;
        }
    }
}
