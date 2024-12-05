package com.example.heatstrokealertapp;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText searchBar;
    private ListView cityListView;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> cityAdapter;
    private ArrayList<String> currentCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        searchBar = findViewById(R.id.search_bar);
        cityListView = findViewById(R.id.city_list);

        // Initialize the database helper and currentCities list
        dbHelper = new DatabaseHelper(this);
        currentCities = new ArrayList<>();

        // Set up the adapter for ListView
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentCities);
        cityListView.setAdapter(cityAdapter);

        // Fetch and display all cities initially
        currentCities.addAll(dbHelper.getCities(""));  // Pass empty string to fetch all cities
        cityAdapter.notifyDataSetChanged();

        // Listen to search input and update list
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = searchBar.getText().toString().trim();

                // If query is empty, show all cities
                if (query.isEmpty()) {
                    ArrayList<String> cities = dbHelper.getCities("");  // Pass empty string for all cities
                    currentCities.clear();
                    currentCities.addAll(cities);
                    cityAdapter.notifyDataSetChanged();
                } else {
                    // Filter cities based on the query
                    ArrayList<String> cities = dbHelper.getCities(query);
                    if (cities.isEmpty()) {
                        Toast.makeText(SearchActivity.this, "No cities found!", Toast.LENGTH_SHORT).show();
                    }

                    currentCities.clear();
                    currentCities.addAll(cities);
                    cityAdapter.notifyDataSetChanged();  // Refresh ListView
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}
