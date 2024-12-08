package com.example.heatstrokealertapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ListView cityListView; // ListView to display the cities
    private DatabaseHelper dbHelper; // Database helper to fetch cities
    private ArrayAdapter<String> cityAdapter; // Adapter for ListView
    private ArrayList<String> currentCities; // List of cities

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views and objects
        cityListView = findViewById(R.id.city_list);
        dbHelper = new DatabaseHelper(this);
        currentCities = new ArrayList<>();

        // Set up the adapter to bind data to ListView
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentCities);
        cityListView.setAdapter(cityAdapter);

        // Fetch cities from database and update the ListView
        Log.d("SearchActivity", "Fetching all cities...");
        currentCities.addAll(dbHelper.getCities(""));  // Pass empty string to fetch all cities
        cityAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the ListView

        // Set an item click listener for the ListView
        cityListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected city from the ListView
            String selectedCity = currentCities.get(position);

            // Create an Intent to send the selected city back to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_text", selectedCity);  // Add the selected city to the Intent

            // Set the result to RESULT_OK and return the result
            setResult(RESULT_OK, resultIntent);

            // Close the SearchActivity and return to MainActivity
            finish();
        });
    }
}
