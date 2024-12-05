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
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    // Declare views at class level to make them accessible in the callback
    private TextView cityNameTextView, tMinMaxTextView, FeelsLikeTempTextView, HumidityTextView, VisibilityTextView, PressureTextTextView, SunriseTextView, SunSetTextView, WindDegTextView, WindSpeedTextView, UvIndexTextTextView, DewPointTextView;
    private RecyclerView recyclerView, hourlyWeatherRecyclerView;

    private DrawerLayout drawerLayout;
    private ImageButton OpenSearchBtn,OpenNotificationBtn;
    private FrameLayout rightPopupLayout,leftPopupLayout;

    LinearLayoutManager linearLayoutManager;

    // Global cityName variable
    private String cityName = "";
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
        UvIndexTextTextView = findViewById(R.id.UvIndexText);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewDays);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        hourlyWeatherRecyclerView = findViewById(R.id.recyclerViewHours);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hourlyWeatherRecyclerView.setLayoutManager(layoutManager);


        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        OpenNotificationBtn = findViewById(R.id.NotificationBtn);
        OpenSearchBtn = findViewById(R.id.SearchBtn);
        rightPopupLayout = findViewById(R.id.notification_popup);
        leftPopupLayout = findViewById(R.id.search_popup);


        // Open the pop-up when the first button is clicked
        OpenNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slide the pop-up in from the right
                rightPopupLayout.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(rightPopupLayout);
            }
        });

        // Handle clicks inside the pop-up using the reusable logic from NotificationsActivity
        rightPopupLayout.findViewById(R.id.bell_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the handleClickAction method from NotificationsActivity
                // This method is reused for the pop-up button action
                RighthandleClickAction(this);
            }
        });


        // Other action for the second button
        OpenSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slide the pop-up in from the right
                leftPopupLayout.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(leftPopupLayout);
            }
        });




        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
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
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onSuccess(ArrayList<ArrayList<String>> hourlyWeatherDataList, ArrayList<ArrayList<String>> weatherDataList, String weatherMain, double temp, double feelsLike, double dewPoint, double pressure, int humidity, double Uv, double visibility, double windSpeed, int windDeg, String sunrise, String sunset, double minTempC, double maxTempC, double avgTempC, int avgHumidity) {

                                UvIndexTextTextView.setText(Uv + "");
                                tMinMaxTextView.setText(String.format(Locale.US, "H:%.1f° L:%.1f° %s", maxTempC, minTempC, weatherMain));
                                FeelsLikeTempTextView.setText(feelsLike + "°C");
                                HumidityTextView.setText(humidity + "%");
                                DewPointTextView.setText(String.format(Locale.US, "The dew point is %.1f°C", dewPoint));
                                VisibilityTextView.setText(visibility + "KM");
                                PressureTextTextView.setText(pressure + "\n   hPa");
                                WindDegTextView.setText(windDeg + "°");
                                WindSpeedTextView.setText(windSpeed + " m/s");
                                SunriseTextView.setText(sunrise);
                                SunSetTextView.setText(sunset);


                                // Initialize hourlyWeatherList to store HourlyWeather objects
                                List<HourlyWeather> hourlyWeatherList = new ArrayList<>();

                                // Assuming hourlyWeatherDataList is an ArrayList of ArrayList<String> where each inner ArrayList represents an hour's data
                                for (ArrayList<String> hourData : hourlyWeatherDataList) {
                                    // Ensure the hourData has at least 3 elements (time, temp, and humidity)
                                    if (hourData.size() >= 3) {
                                        String hourtime = hourData.get(0);  // Time (e.g., "4:30 AM")
                                        double hourtempC = Double.parseDouble(hourData.get(1));  // Temperature (e.g., "29.0")
                                        int hourheatindex = Integer.parseInt(hourData.get(2));  // Humidity (e.g., "60")
                                        String IconPath = classifyHeatIndex(hourheatindex);

                                        // Create a new HourlyWeather object and add it to the list
                                        hourlyWeatherList.add(new HourlyWeather(hourtime, hourtempC, IconPath));
                                    }
                                }

                                // Create the adapter with the populated hourlyWeatherList
                                HourlyWeatherAdapter hourlyWeatherAdapter = new HourlyWeatherAdapter(hourlyWeatherList);
                                hourlyWeatherRecyclerView.setAdapter(hourlyWeatherAdapter);




                                // Convert weatherDataList to a list of WeatherItem objects
                                List<WeatherItem> weatherItems = new ArrayList<>();
                                for (ArrayList<String> dayData : weatherDataList) {
                                    String date = dayData.get(0);
                                    String avgTemp = dayData.get(1); // avg temperature, you could use this instead of max/min
                                    double minTemp = Double.parseDouble(dayData.get(2));
                                    double maxTemp = Double.parseDouble(dayData.get(3));
                                    int forecastavgHumidity = Integer.parseInt((dayData.get(4)));
                                    double heatIndex = calculateHeatIndex(Double.parseDouble(avgTemp),forecastavgHumidity);
                                    String iconPath = classifyHeatIndex(heatIndex);


                                    // Create a new WeatherItem for each day and add it to the list
                                    weatherItems.add(new WeatherItem(date, maxTemp, minTemp, forecastavgHumidity, iconPath));
                                }

                                // Set up the RecyclerView with the adapter
                                WeatherAdapter weatherAdapter = new WeatherAdapter(weatherItems);
                                recyclerView.setAdapter(weatherAdapter);
                            }


                            @Override
                            public void onError(String error) {
                                Toast.makeText(MainActivity.this, "Weather API Error: " + error, Toast.LENGTH_SHORT).show();
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

    // Method to classify heat index in Celsius
    public static String classifyHeatIndex(double heatIndexCelsius) {
        // Classify based on the given heat index value in Celsius and return the drawable path
        if (heatIndexCelsius <= 21.1) {
            return "safe"; // Corresponds to res/drawable/safe.png
        } else if (heatIndexCelsius > 21.1 && heatIndexCelsius <= 26.7) {
            return "caution"; // Corresponds to res/drawable/caution.png
        } else if (heatIndexCelsius > 26.7 && heatIndexCelsius <= 32.2) {
            return "ext_caution"; // Corresponds to res/drawable/ext_caution.png
        } else if (heatIndexCelsius > 32.2 && heatIndexCelsius <= 39.4) {
            return "danger"; // Corresponds to res/drawable/danger.png
        } else {
            return "extreme_danger"; // Corresponds to res/drawable/extreme_danger.png
        }
    }

    public static double calculateHeatIndex(double temperatureCelsius, double humidity) {
        // Applying the heat index formula for Celsius
        double HI = -8.784694755 +
                1.61139411 * temperatureCelsius +
                2.338548838 * humidity -
                0.14611605 * temperatureCelsius * humidity -
                0.012308094 * Math.pow(temperatureCelsius, 2) -
                0.016424828 * Math.pow(humidity, 2) +
                0.002211732 * Math.pow(temperatureCelsius, 2) * humidity +
                0.00072546 * Math.pow(humidity, 2) * temperatureCelsius -
                0.00000358 * Math.pow(temperatureCelsius, 2) * Math.pow(humidity, 2);

        // Return the calculated heat index in Celsius
        return HI;
    }

    // This is the reused logic from NotificationsActivity
    public void RighthandleClickAction(View.OnClickListener onClickListener) {
        // Logic that was in NotificationsActivity (showing a Toast as an example)
        Toast.makeText(MainActivity.this, classifyHeatIndex(Double.parseDouble("24")), Toast.LENGTH_SHORT).show();
    }


}
