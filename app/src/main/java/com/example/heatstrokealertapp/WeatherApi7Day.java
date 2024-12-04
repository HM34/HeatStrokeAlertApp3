package com.example.heatstrokealertapp;

import android.os.AsyncTask;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApi7Day {

    private static final String API_KEY = "bee3aae3ce607a52210e03d094ea75fa"; // Replace with your OpenWeatherMap API key
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/onecall"; // One Call API for 7-day forecast

    public static void getWeatherData(double latitude, double longitude, final WeatherDataCallback callback) {
        new AsyncTask<Double, Void, String>() {
            @Override
            protected String doInBackground(Double... params) {
                double lat = params[0];
                double lon = params[1];
                // Construct the URL to request the 7-day forecast from the One Call API
                String urlString = BASE_URL + "?lat=" + lat + "&lon=" + lon + "&exclude=current,minutely,hourly,alerts&units=metric&appid=" + API_KEY;

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

                        // Get the daily forecast data (7 days)
                        JSONArray daily = jsonResponse.getJSONArray("daily");
                        StringBuilder forecast = new StringBuilder();

                        // Loop through the daily array to extract temperature and humidity for each day
                        for (int i = 0; i < daily.length(); i++) {
                            JSONObject day = daily.getJSONObject(i);

                            // Get the date (convert from Unix timestamp)
                            long dateTimestamp = day.getLong("dt");
                            String date = new java.text.SimpleDateFormat("yyyy-MM-dd")
                                    .format(new java.util.Date(dateTimestamp * 1000));  // Convert to milliseconds

                            // Get temperature (max and min) and humidity
                            double tempMin = day.getJSONObject("temp").getDouble("min");
                            double tempMax = day.getJSONObject("temp").getDouble("max");
                            int humidity = day.getInt("humidity");

                            // Build the forecast string
                            forecast.append("Date: ").append(date)
                                    .append("\nTemp: ").append(tempMin).append("°C - ").append(tempMax).append("°C")
                                    .append("\nHumidity: ").append(humidity).append("%\n\n");
                        }

                        // Return forecast data via callback
                        callback.onSuccess(forecast.toString());

                    } catch (Exception e) {
                        callback.onError("Failed to parse data");
                    }
                } else {
                    callback.onError("Failed to fetch data");
                }
            }
        }.execute(latitude, longitude);
    }

    public interface WeatherDataCallback {
        void onSuccess(String forecast);
        void onError(String error);
    }
}
