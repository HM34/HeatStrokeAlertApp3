package com.example.heatstrokealertapp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class WeatherApi7Day {

    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your OpenWeatherMap API key

    // Retrofit client instance
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    // Define the API interface
    public interface WeatherApiService {
        @GET("onecall")
        Call<WeatherResponse> getForecast(
                @Query("lat") double latitude,
                @Query("lon") double longitude,
                @Query("exclude") String exclude,
                @Query("units") String units,
                @Query("appid") String apiKey
        );
    }

    // Fetch 7-day forecast
    public static void get7DayForecast(double latitude, double longitude, final ForecastListener listener) {
        WeatherApiService apiService = retrofit.create(WeatherApiService.class);
        Call<WeatherResponse> call = apiService.getForecast(latitude, longitude, "current,minutely,hourly,alerts", "metric", API_KEY);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onForecastReceived(response.body().daily);
                } else {
                    listener.onFailure("Failed to fetch data");
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                listener.onFailure(t.getMessage());
            }
        });
    }

    // Interface for callback
    public interface ForecastListener {
        void onForecastReceived(WeatherResponse.Daily[] dailyForecast);
        void onFailure(String error);
    }
}

