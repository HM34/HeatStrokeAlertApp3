package com.example.heatstrokealertapp;

import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Forecast7Day {

    private static final String API_KEY = "bee3aae3ce607a52210e03d094ea75fa"; // Replace with your OpenWeatherMap API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/forecast"; // Correct endpoint for 7-day forecast

    public static void get7DayForecast(double latitude, double longitude, final WeatherDataCallback callback) {
        new AsyncTask<Double, Void, String>() {
            @Override
            protected String doInBackground(Double... params) {
                double lat = params[0];
                double lon = params[1];
                String urlString = BASE_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric"; // Use 'metric' for Celsius
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

                        // Extract the 'list' array from the response
                        JSONArray forecastArray = jsonResponse.getJSONArray("list");

                        // Use a map to store data for each day
                        JSONObject dailyData = new JSONObject();

                        // Loop through the forecast list
                        for (int i = 0; i < forecastArray.length(); i++) {
                            JSONObject forecast = forecastArray.getJSONObject(i);

                            // Extract timestamp and convert to readable date
                            long timestamp = forecast.getLong("dt");
                            String date = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                    .format(new java.util.Date(timestamp * 1000)); // Convert to milliseconds

                            // Extract temperature and humidity data
                            double tempMin = forecast.getJSONObject("main").getDouble("temp_min");
                            double tempMax = forecast.getJSONObject("main").getDouble("temp_max");
                            int humidity = forecast.getJSONObject("main").getInt("humidity");

                            // Store daily data in the map, update if necessary
                            if (!dailyData.has(date)) {
                                JSONObject dailyForecast = new JSONObject();
                                dailyForecast.put("temp_min", tempMin);
                                dailyForecast.put("temp_max", tempMax);
                                dailyForecast.put("humidity", humidity);
                                dailyData.put(date, dailyForecast);
                            } else {
                                JSONObject dailyForecast = dailyData.getJSONObject(date);
                                dailyForecast.put("temp_min", Math.min(dailyForecast.getDouble("temp_min"), tempMin));
                                dailyForecast.put("temp_max", Math.max(dailyForecast.getDouble("temp_max"), tempMax));
                                dailyForecast.put("humidity", Math.max(dailyForecast.getInt("humidity"), humidity));
                            }
                        }

                        // Send back the processed data to the callback
                        for (int i = 0; i < dailyData.names().length(); i++) {
                            String date = dailyData.names().getString(i);
                            JSONObject dailyForecast = dailyData.getJSONObject(date);
                            callback.onSuccess(date, dailyForecast.getDouble("temp_min"),
                                    dailyForecast.getDouble("temp_max"),
                                    dailyForecast.getInt("humidity"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onError("Failed to parse data");
                    }
                } else {
                    callback.onError("Failed to fetch data");
                }
            }
        }.execute(latitude, longitude);
    }

    // Interface for callback
    public interface WeatherDataCallback {
        void onSuccess(String date, double tempMin, double tempMax, int humidity); // Only necessary data
        void onError(String error);
    }
}
