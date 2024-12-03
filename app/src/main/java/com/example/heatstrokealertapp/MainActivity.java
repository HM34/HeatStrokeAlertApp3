package com.example.heatstrokealertapp;

import android.Manifest;
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
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // FusedLocationProviderClient instance
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Toast.makeText(this, "Location permission is required to access location", Toast.LENGTH_SHORT).show();

                // Optionally, send the user to settings to manually grant permission
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    // Get the user's current location
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
                        Toast.makeText(MainActivity.this, "Location: " + latitude + ", " + longitude, Toast.LENGTH_LONG).show();

                        // Now, get the city name using reverse geocoding
                        getCityNameFromLocation(latitude, longitude);
                    } else {
                        // If FusedLocationProviderClient fails to get location, fall back to LocationManager
                        Toast.makeText(MainActivity.this, "Unable to get location from FusedLocationProvider. Trying LocationManager.", Toast.LENGTH_SHORT).show();
                        getLocationUsingLocationManager();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // If FusedLocationProviderClient fails completely
                    Toast.makeText(MainActivity.this, "Failed to get location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    getLocationUsingLocationManager(); // Fallback to LocationManager
                }
            });
        } else {
            Toast.makeText(this, "Permission denied, cannot get location.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocationUsingLocationManager() {
        // Use LocationManager as a fallback
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            try {
                // Use the best provider (GPS or Network)
                String provider = locationManager.getBestProvider(new Criteria(), true);
                if (provider != null) {
                    locationManager.requestSingleUpdate(provider, new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Toast.makeText(MainActivity.this, "Location (from LocationManager): " + latitude + ", " + longitude, Toast.LENGTH_LONG).show();

                                // Now, get the city name using reverse geocoding
                                getCityNameFromLocation(latitude, longitude);
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {}

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {}

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {}
                    }, null); // Here, no need for a Looper, as it's just a one-time update
                } else {
                    Toast.makeText(MainActivity.this, "No suitable provider found.", Toast.LENGTH_SHORT).show();
                }
            } catch (SecurityException e) {
                Toast.makeText(MainActivity.this, "Permission denied for LocationManager.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // Get the city name from latitude and longitude using reverse geocoding

    private void getCityNameFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String cityName = addresses.get(0).getLocality();
                Toast.makeText(MainActivity.this, "City: " + cityName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Unable to get city name.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Geocoder error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
