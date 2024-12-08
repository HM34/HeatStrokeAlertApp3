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
    private static final int REQUEST_CODE_SEARCH = 1;
    // Declare views at class level to make them accessible in the callback
    private TextView cityNameTextView, tMinMaxTextView, FeelsLikeTempTextView, HumidityTextView, VisibilityTextView, PressureTextTextView, SunriseTextView, SunSetTextView, WindDegTextView, WindSpeedTextView, UvIndexTextTextView, DewPointTextView;
    private RecyclerView recyclerView, hourlyWeatherRecyclerView;

    private DrawerLayout drawerLayout;
    private ImageButton OpenSearchBtn,OpenNotificationBtn;
    private FrameLayout rightPopupLayout,leftPopupLayout;

    private WeatherUtils weatherUtils;

    private LocationUtils locationUtils;





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

        // Initialize all your views (TextViews and RecyclerViews)
        cityNameTextView = findViewById(R.id.CityName);
        tMinMaxTextView = findViewById(R.id.TMinMax);
        FeelsLikeTempTextView = findViewById(R.id.FeelsLikeTemp);
        HumidityTextView = findViewById(R.id.HumidityText);
        DewPointTextView = findViewById(R.id.DewPointText);
        VisibilityTextView = findViewById(R.id.VisibiltyText);
        PressureTextTextView = findViewById(R.id.PressureText);
        WindDegTextView = findViewById(R.id.WindDegText);
        WindSpeedTextView = findViewById(R.id.WindSpeedText);
        UvIndexTextTextView = findViewById(R.id.UvIndexText);
        SunriseTextView = findViewById(R.id.SunriseText);
        SunSetTextView = findViewById(R.id.SunSetText);

        // Initialize views
        cityNameTextView = findViewById(R.id.CityName);
        hourlyWeatherRecyclerView = findViewById(R.id.recyclerViewHours);
        recyclerView = findViewById(R.id.recyclerViewDays);

        // Initialize LocationUtils
        locationUtils = new LocationUtils((Context) this, (LocationUtils.OnLocationRetrievedListener) this);  // Pass 'this' to handle location retrieval

        locationUtils.checkLocationPermission();

        // Create an instance of WeatherUtils and pass necessary views
        weatherUtils = new WeatherUtils(this, cityNameTextView, tMinMaxTextView, FeelsLikeTempTextView,
                HumidityTextView, DewPointTextView, VisibilityTextView, PressureTextTextView, WindDegTextView,
                WindSpeedTextView, UvIndexTextTextView, SunriseTextView, SunSetTextView, hourlyWeatherRecyclerView,
                recyclerView);

        // Fetch location and weather data
        double latitude = 40.7128; // Example latitude
        double longitude = -74.0060; // Example longitude
        weatherUtils.fetchCityName(latitude, longitude);

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

            private void RighthandleClickAction(View.OnClickListener onClickListener) {
            }
        });


        // Open SearchActivity and wait for result when the search button is clicked
        OpenSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Launch SearchActivity and wait for result
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SEARCH);
                } catch (Exception e) {
                    // Show a Toast message if an error occurs
                    Toast.makeText(MainActivity.this, "Failed to open SearchActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
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



    // This method will be called when SearchActivity finishes and sends back the result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is from SearchActivity
        if (requestCode == REQUEST_CODE_SEARCH && resultCode == RESULT_OK) {
            // Get the selected text from SearchActivity using the key you used to send it back
            String selectedText = data.getStringExtra("selected_text");

            // Do something with the selected text, like setting it in a TextView
            if (selectedText != null) {
                // For example, set it in the TextView that displays the city name
                cityNameTextView.setText(selectedText);
                // Create an instance of WeatherUtils and call fetchWeatherData with the city name
                weatherUtils.fetchWeatherData(selectedText);
            }
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
                        weatherUtils.fetchCityName(latitude, longitude);
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
                                weatherUtils.fetchCityName(latitude, longitude);
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


   

}
