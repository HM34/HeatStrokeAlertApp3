package com.example.heatstrokealertapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeonamesService {
    @GET("searchJSON")
    Call<GeonamesResponse> searchCities(
            @Query("q") String query,          // City search query
            @Query("maxRows") int maxRows,     // How many results to return
            @Query("username") String username // Geonames API username
    );
}
