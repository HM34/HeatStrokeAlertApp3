package com.example.heatstrokealertapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationUtils.OnLocationRetrievedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_SEARCH = 1;

    private TextView cityNameTextView, tMinMaxTextView, FeelsLikeTempTextView, HumidityTextView, VisibilityTextView,
            PressureTextTextView, SunriseTextView, SunSetTextView, WindDegTextView, WindSpeedTextView, UvIndexTextTextView, DewPointTextView;
    private RecyclerView recyclerView, hourlyWeatherRecyclerView;
    private DrawerLayout drawerLayout;
    private ImageButton OpenSearchBtn, OpenNotificationBtn;
    private FrameLayout rightPopupLayout, leftPopupLayout;

    private WeatherUtils weatherUtils;
    private LocationUtils locationUtils;
    private String cityName = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
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

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewDays);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        hourlyWeatherRecyclerView = findViewById(R.id.recyclerViewHours);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hourlyWeatherRecyclerView.setLayoutManager(layoutManager);

        // Initialize LocationUtils
        locationUtils = new LocationUtils(this, this);
        locationUtils.checkLocationPermission();

        // Initialize WeatherUtils
        weatherUtils = new WeatherUtils(this, cityNameTextView, tMinMaxTextView, FeelsLikeTempTextView,
                HumidityTextView, DewPointTextView, VisibilityTextView, PressureTextTextView, WindDegTextView,
                WindSpeedTextView, UvIndexTextTextView, SunriseTextView, SunSetTextView, hourlyWeatherRecyclerView,
                recyclerView);

        // Drawer layout and button setup
        drawerLayout = findViewById(R.id.drawer_layout);
        OpenNotificationBtn = findViewById(R.id.NotificationBtn);
        OpenSearchBtn = findViewById(R.id.SearchBtn);
        rightPopupLayout = findViewById(R.id.notification_popup);
        leftPopupLayout = findViewById(R.id.search_popup);

        // notificationRecyclerView
        RecyclerView notificationRecyclerView = findViewById(R.id.recycler_view_notifications);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample notifications
        List<NotificationItem> notifications = new ArrayList<>();
        notifications.add(new NotificationItem("10 minutes ago", "A sunny day in your location, consider wearing UV protection", R.drawable.caution));
        notifications.add(new NotificationItem("1 day ago", "A cloudy day will occur all day long, don't worry about the heat of the sun", R.drawable.safe));
        notifications.add(new NotificationItem("2 days ago", "Potential for rain today is 84%, don't forget your umbrella.", R.drawable.safe));

        // Attach adapter
        NotificationAdapter adapter = new NotificationAdapter(notifications);
        notificationRecyclerView.setAdapter(adapter);

        // Notification button click handler
        OpenNotificationBtn.setOnClickListener(v -> {
            if (drawerLayout != null) {
                rightPopupLayout.setVisibility(View.VISIBLE);
                drawerLayout.openDrawer(GravityCompat.END); // Opens from the right
            } else {
                Log.e("MainActivity", "DrawerLayout or rightPopupLayout is null!");
            }
        });

        // Search button click handler
        OpenSearchBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SEARCH);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Failed to open SearchActivity: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onLocationRetrieved(double latitude, double longitude, String cityName) {
        // When location is retrieved, fetch weather data
        cityNameTextView.setText(cityName);
        weatherUtils.fetchWeatherData(cityName);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SEARCH && resultCode == RESULT_OK) {
            String selectedText = data.getStringExtra("selected_text");
            if (selectedText != null) {
                cityNameTextView.setText(selectedText);
                weatherUtils.fetchWeatherData(selectedText);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestLocationPermission() {
        new AlertDialog.Builder(this)
                .setMessage("We need location permission to show weather data for your area.")
                .setPositiveButton("Grant", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
