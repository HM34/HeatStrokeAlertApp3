package com.example.heatstrokealertapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Display a Toast message as a basic action
        Toast.makeText(this, "Welcome to HeatStrokeAlertApp", Toast.LENGTH_LONG).show();
    }
}
