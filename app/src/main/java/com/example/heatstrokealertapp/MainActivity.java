package com.example.heatstrokealertapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {



    // Declare views at class level to make them accessible in the callback
    private TextView cityNameTextView, tMinMaxTextView, FeelsLikeTempTextView, HumidityTextView,
            VisibilityTextView, PressureTextTextView, SunriseTextView, SunSetTextView,
            WindDegTextView, WindSpeedTextView, TimeZoneTextView, DewPointTextView;

    private RecyclerView recyclerView;
    private WeatherAdapter weatherAdapter;



    // Global cityName variable
    private String cityName = "";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // FusedLocationProviderClient instance
    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
        cityNameTextView = findViewById(R.id.CityName);
        tMinMaxTextView = findViewById(R.id.TMinMax);
        FeelsLikeTempTextView = findViewById(R.id.FeelsLikeTemp);
        HumidityTextView = findViewById(R.id.HumidityText);
        DewPointTextView = findViewById(R.id.DewPointText);

        VisibilityTextView = findViewById(R.id.VisibiltyText);
        PressureTextTextView = findViewById(R.id.PressureText);
        SunriseTextView = findViewById(R.id.SunriseText);
        SunSetTextView = findViewById(R.id.SunSetText);
        WindDegTextView = findViewById(R.id.WindDegText);
        WindSpeedTextView = findViewById(R.id.WindSpeedText);
        TimeZoneTextView = findViewById(R.id.TimeZoneText);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, proceed to get location
            getUserLocation();
        }


    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to get location
                getUserLocation();
            } else {
                // Permission denied, show a message or handle accordingly
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    // Get the user's current location
    private void getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Attempt to get location using FusedLocationProviderClient
            Task<Location> locationResult = fusedLocationClient.getLastLocation();

            locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(@NonNull Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        // Now, get the city name using reverse geocoding
                        getCityNameFromLocation(latitude, longitude);
                    } else {
                        getLocationUsingLocationManager();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    getLocationUsingLocationManager(); // Fallback to LocationManager
                }
            });
        }
    }

    private void getLocationUsingLocationManager() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            // Check if location services are enabled
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGpsEnabled && !isNetworkEnabled) {
                Toast.makeText(this, "Location services are disabled. Please enable them in settings.", Toast.LENGTH_SHORT).show();
                return; // Exit if no provider is available
            }

            try {
                // Use the best provider (GPS or Network)
                String provider = locationManager.getBestProvider(new Criteria(), true);

                if (provider != null) {
                    // Request single location update from the selected provider
                    locationManager.requestSingleUpdate(provider, new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                getCityNameFromLocation(latitude, longitude); // Call geocoder to get city
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {
                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {
                            // Handle case if the provider is disabled
                        }
                    }, null); // No need for Looper in this case (single update)
                }
            } catch (SecurityException e) {
                // Handle security exception
                Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Get the city name from latitude and longitude using reverse geocoding
    private void getCityNameFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    cityName = addresses.get(0).getLocality(); // Update the global cityName variable
                    runOnUiThread(() -> {
                        cityNameTextView.setText(cityName);

                        // today api call
                        WeatherApi.getWeatherData(cityName, new WeatherApi.WeatherDataCallback() {
                            @Override
                            public void onSuccess(String weatherDescription, String weatherMain, double temp, double feelsLike, double tempMin,
                                                  double tempMax, double pressure, int humidity, int visibility, double windSpeed, int windDeg,
                                                  long sunrise, long sunset, int timezone) {
                                // Format timezone to +3:00 format
                                String formattedTimezone = formatTimezone(timezone);

                                // Format sunrise and sunset to time (AM/PM)
                                String formattedSunrise = formatTimestamp(sunrise);
                                String formattedSunset = formatTimestamp(sunset);

                                // Format visibility in meters to kilometers (optional)
                                String formattedVisibility = String.format(Locale.US, "%.1f km", visibility / 1000.0); // Convert meters to km

                                // Calculate the dew point
                                double dewPoint = calculateDewPoint(temp, humidity);

                                // Update the UI with the weather data
                                TimeZoneTextView.setText(formattedTimezone);
                                tMinMaxTextView.setText(String.format(Locale.US, "H:%.1f° L:%.1f° %s", tempMax, tempMin, weatherMain));
                                FeelsLikeTempTextView.setText(feelsLike + "°C");
                                HumidityTextView.setText(humidity + "%");
                                DewPointTextView.setText(String.format(Locale.US, "The dew point is %.1f°C", dewPoint));
                                VisibilityTextView.setText(formattedVisibility);
                                PressureTextTextView.setText(pressure + "\n   hPa");
                                WindDegTextView.setText(windDeg + "°");
                                WindSpeedTextView.setText(windSpeed + " m/s");
                                SunriseTextView.setText(formattedSunrise);
                                SunSetTextView.setText(formattedSunset);
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(MainActivity.this, "Weather API Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                        // Fetch 7-day forecast
                        WeatherApi7Day.get7DayForecast(latitude, longitude, new WeatherApi7Day.ForecastListener() {
                            @Override
                            public void onForecastReceived(WeatherResponse.Daily[] dailyForecast) {
                                // Loop through the forecast data and log or display it
                                for (WeatherResponse.Daily day : dailyForecast) {
                                    String date = new java.text.SimpleDateFormat("yyyy-MM-dd")
                                            .format(new java.util.Date(day.dt * 1000)); // Convert Unix time to date
                                    float tempMax = day.temp.max;
                                    float tempMin = day.temp.min;
                                    int humidity = day.humidity;

                                    // Log or display the data
                                    Log.d("Weather", "Date: " + date + ", Max Temp: " + tempMax + "°C, Min Temp: " + tempMin + "°C, Humidity: " + humidity + "%");
                                }
                            }

                            @Override
                            public void onFailure(String error) {
                                Log.e("Weather", "Error: " + error);
                            }
                        });

                    });
                } else {
                    cityName = "City not found"; // Default if not found
                    runOnUiThread(() -> {
                        cityNameTextView.setText(cityName);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                cityName = "Geocoder error"; // Error message if geocoder fails
                runOnUiThread(() -> {
                    cityNameTextView.setText(cityName);
                });
            }
        }).start();
    }

    // Format timezone in +3:00 format
    private String formatTimezone(int timezoneInSeconds) {
        int hours = timezoneInSeconds / 3600;
        int minutes = (timezoneInSeconds % 3600) / 60;
        return String.format(Locale.US, "%+03d:%02d", hours, minutes);
    }

    // Format a Unix timestamp to a readable time format (e.g., 5:00 AM)
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        Date date = new Date(timestamp * 1000); // Convert seconds to milliseconds
        return sdf.format(date);
    }

    // Calculate the dew point from temperature and humidity
    private double calculateDewPoint(double temp, int humidity) {
        return temp - ((100 - humidity) / 5.0);
    }


}
