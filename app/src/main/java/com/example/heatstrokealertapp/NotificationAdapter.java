package com.example.heatstrokealertapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<NotificationItem> notifications;
    private static final String TAG = "NotificationAdapter";

    public NotificationAdapter(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);

        // Set icon if valid, else hide
        if (notification.getIconResId() != 0) {
            holder.icon.setImageResource(notification.getIconResId());
            holder.icon.setVisibility(View.VISIBLE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }

        holder.time.setText(notification.getTime());
        holder.message.setText(notification.getMessage());

        Log.d(TAG, "Binding item at position: " + position + ", Message: " + notification.getMessage());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Total items: " + notifications.size());
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView time;
        TextView message;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.notification_icon);
            time = itemView.findViewById(R.id.notification_time);
            message = itemView.findViewById(R.id.notification_message);
        }
    }
}
