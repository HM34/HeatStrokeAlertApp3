package com.example.heatstrokealertapp;// NotificationsActivity.java
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications); // This layout is your pop-up layout

        // Initialize button click listeners or any actions inside NotificationsActivity
        findViewById(R.id.bell_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the button click here (this is the code you want to reuse)
                RighthandleClickAction();
            }
        });
    }

    // Reusable method containing the logic for button clicks
    public void RighthandleClickAction() {
        // Your logic from NotificationsActivity goes here
        // For example, showing a Toast message
        Toast.makeText(NotificationsActivity.this, "Button clicked in the pop-up!", Toast.LENGTH_SHORT).show();
    }
}
