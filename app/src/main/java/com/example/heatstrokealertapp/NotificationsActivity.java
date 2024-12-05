package com.example.heatstrokealertapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Find the close button and set a click listener
        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the activity when the close button is clicked
                finish();
            }
        });

        // Example of handling other interactions
        ImageView bellIcon = findViewById(R.id.bell_icon);
        bellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NotificationsActivity.this, "Bell icon clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView notificationIcon = findViewById(R.id.notification_icon);
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NotificationsActivity.this, "Notification icon clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
