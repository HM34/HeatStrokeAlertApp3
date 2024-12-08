package com.example.heatstrokealertapp;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GeonamesResponse {
    @SerializedName("geonames")
    private List<City> cities;

    public List<City> getCities() {
        return cities;
    }

    public static class City {
        @SerializedName("name")
        private String name;  // City name

        @SerializedName("countryName")
        private String country;  // Country name where the city is located

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }
    }
}
