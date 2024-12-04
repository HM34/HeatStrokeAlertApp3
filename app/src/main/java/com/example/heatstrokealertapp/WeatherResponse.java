package com.example.heatstrokealertapp;

public class WeatherResponse {
    public Daily[] daily;

    public static class Daily {
        public long dt; // Date in Unix time
        public Temp temp;
        public int humidity;

        public static class Temp {
            public float max;
            public float min;
        }
    }
}
