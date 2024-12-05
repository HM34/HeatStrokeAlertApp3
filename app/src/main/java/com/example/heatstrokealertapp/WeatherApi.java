package com.example.heatstrokealertapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WeatherApi {
    private static final String API_KEY = "d61006078fa84f45bf3224454240412"; // Replace with your WeatherAPI key
    private static final String BASE_URL = "https://api.weatherapi.com/v1/forecast.json";

    public static void getWeatherData(String cityName, final WeatherDataCallback callback) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String city = params[0];
                // Correct the URL and remove the extra space after the API key
                String urlString = BASE_URL + "?key=" + API_KEY + "&q=" + city + "&days=7&aqi=no&alerts=no";

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
                        JSONObject currentWeather = jsonResponse.getJSONObject("current");


                        // Extract the required data
                        String weatherMain = currentWeather.getJSONObject("condition").getString("text");
                        double temp = currentWeather.getDouble("temp_c");
                        double feelsLike = currentWeather.getDouble("feelslike_c");
                        int humidity = currentWeather.getInt("humidity");
                        double pressure = currentWeather.getDouble("pressure_mb");
                        double windSpeed = currentWeather.getDouble("wind_kph");
                        int windDeg = currentWeather.getInt("wind_degree");
                        double dewPoint = currentWeather.getDouble("dewpoint_c");
                        double visibility = currentWeather.getDouble("vis_km");
                        double Uv = currentWeather.getDouble("uv");

                        JSONObject forecast = jsonResponse.getJSONObject("forecast");
                        JSONArray forecastday = forecast.getJSONArray("forecastday");
                        JSONObject today = forecastday.getJSONObject(0).getJSONObject("astro");
                        String sunrise = today.getString("sunrise");
                        String sunset = today.getString("sunset");

                        JSONObject dayWeather = forecastday.getJSONObject(0).getJSONObject("day");
                        double minTempC = dayWeather.getDouble("mintemp_c");
                        double maxTempC = dayWeather.getDouble("maxtemp_c");
                        double avgTempC = dayWeather.getDouble("avgtemp_c");
                        int avgHumidity = dayWeather.getInt("avghumidity");



                        if (forecastday.length() > 0) {
                            // Get the first forecast day
                            JSONObject firstForecastDay = forecastday.getJSONObject(0);

                            // Get the hour array for this first day
                            JSONArray hourArray = firstForecastDay.getJSONArray("hour");

                            // List to store hourly weather data
                            ArrayList<HourlyWeather> hourlyWeatherList = new ArrayList<>();

                            // Loop through each hour in the first day's forecast
                            for (int j = 0; j < hourArray.length(); j++) {
                                JSONObject hourData = hourArray.getJSONObject(j);

                                // Extract time, temp_c, and humidity
                                String hourtime = hourData.getString("time");
                                double hourtempC = hourData.getDouble("temp_c");
                                int hourhumidity = hourData.getInt("humidity");

                                // Create a new HourlyWeather object and add it to the list
                                hourlyWeatherList.add(new HourlyWeather(hourtime, hourtempC, hourhumidity));
                            }


                        }

                        // ArrayList to store all the dates
                        ArrayList<ArrayList<String>> weatherDataList = new ArrayList<>();

                        // Iterate through each day in the forecastday array
                        for (int i = 0; i < forecastday.length(); i++) {
                            JSONObject day = forecastday.getJSONObject(i);

                            String forecastdate = day.getString("date"); // Extract the date for each day
                            double forecastavgTempC = day.getJSONObject("day").getDouble("avgtemp_c");
                            double forecastminTempC = day.getJSONObject("day").getDouble("mintemp_c");
                            double forecastmaxTempC = day.getJSONObject("day").getDouble("maxtemp_c");
                            int forecastavgHumidity = day.getJSONObject("day").getInt("avghumidity");

                            // Create a new ArrayList for the current day's data
                            ArrayList<String> dayData = new ArrayList<>();
                            dayData.add(forecastdate);
                            dayData.add(String.valueOf(forecastavgTempC));
                            dayData.add(String.valueOf(forecastminTempC));
                            dayData.add(String.valueOf(forecastmaxTempC));
                            dayData.add(String.valueOf(forecastavgHumidity));

                            // Add the day's data to the main list
                            weatherDataList.add(dayData);
                        }


                        // Pass the extracted data to the callback
                        callback.onSuccess(weatherDataList,weatherMain, temp, feelsLike, dewPoint, pressure, humidity, visibility,Uv, windSpeed, windDeg,sunrise,sunset, minTempC, maxTempC, avgTempC, avgHumidity);

                    } catch (Exception e) {
                        callback.onError("Failed to parse data");
                    }
                } else {
                    callback.onError("Failed to fetch data");
                }
            }
        }.execute(cityName);
    }

    // Interface for callback
    public interface WeatherDataCallback {
        void onSuccess(ArrayList<ArrayList<String>> weatherDataList,String weatherMain, double temp, double feelsLike, double dewPoint, double pressure, int humidity,double Uv, double visibility, double windSpeed, int windDeg,String sunrise,String sunset, double minTempC, double maxTempC ,double avgTempC  ,int avgHumidity);

        void onError(String error);
    }
}
