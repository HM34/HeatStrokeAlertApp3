package com.example.heatstrokealertapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Log.d(TAG, "onCreate executed.");

        RecyclerView recyclerView = findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample data
        List<NotificationItem> notifications = new ArrayList<>();
        notifications.add(new NotificationItem("10 minutes ago", "A sunny day in your location, consider wearing UV protection", R.drawable.caution));
        notifications.add(new NotificationItem("1 day ago", "A cloudy day will occur all day long, don't worry about the heat of the sun", R.drawable.safe));
        notifications.add(new NotificationItem("2 days ago", "Potential for rain today is 84%, don't forget your umbrella.", R.drawable.safe));

        Log.d(TAG, "Notification list size: " + notifications.size());

        // Attach adapter
        NotificationAdapter adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "RecyclerView adapter set.");
    }
}
