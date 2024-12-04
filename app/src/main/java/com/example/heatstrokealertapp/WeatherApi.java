package com.example.heatstrokealertapp;

import android.os.AsyncTask;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApi {

    private static final String API_KEY = "bee3aae3ce607a52210e03d094ea75fa"; // Replace with your OpenWeatherMap API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public static void getWeatherData(String cityName, final WeatherDataCallback callback) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String city = params[0];
                String urlString = BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric"; // Use 'metric' for Celsius
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                    StringBuilder result = new StringBuilder();
                    int data = reader.read();
                    while (data != -1) {
                        result.append((char) data);
                        data = reader.read();
                    }
                    return result.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);

                        // Extracting various weather details
                        String weatherDescription = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
                        String weatherMain = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("main");
                        double temp = jsonResponse.getJSONObject("main").getDouble("temp");
                        double feelsLike = jsonResponse.getJSONObject("main").getDouble("feels_like");
                        double tempMin = jsonResponse.getJSONObject("main").getDouble("temp_min");
                        double tempMax = jsonResponse.getJSONObject("main").getDouble("temp_max");
                        double pressure = jsonResponse.getJSONObject("main").getDouble("pressure");
                        int humidity = jsonResponse.getJSONObject("main").getInt("humidity");
                        int visibility = jsonResponse.getInt("visibility");
                        double windSpeed = jsonResponse.getJSONObject("wind").getDouble("speed");
                        int windDeg = jsonResponse.getJSONObject("wind").getInt("deg");
                        long sunrise = jsonResponse.getJSONObject("sys").getLong("sunrise");
                        long sunset = jsonResponse.getJSONObject("sys").getLong("sunset");
                        int timezone = jsonResponse.getInt("timezone");

                        // Return all values via callback
                        callback.onSuccess(
                                weatherDescription, weatherMain, temp, feelsLike, tempMin, tempMax,
                                pressure, humidity, visibility, windSpeed, windDeg, sunrise, sunset, timezone
                        );
                    } catch (Exception e) {
                        callback.onError("Failed to parse data");
                    }
                } else {
                    callback.onError("Failed to fetch data");
                }
            }
        }.execute(cityName);
    }

    public interface WeatherDataCallback {
        void onSuccess(String weatherDescription, String weatherMain, double temp, double feelsLike, double tempMin,
                       double tempMax, double pressure, int humidity, int visibility, double windSpeed, int windDeg,
                       long sunrise, long sunset, int timezone);

        void onError(String error);
    }
}
