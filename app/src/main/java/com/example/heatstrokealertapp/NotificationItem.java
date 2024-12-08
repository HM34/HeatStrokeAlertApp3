package com.example.heatstrokealertapp;

public class NotificationItem {
    private String time;
    private String message;
    private int iconResId;

    public NotificationItem(String time, String message, int iconResId) {
        this.time = time;
        this.message = message;
        this.iconResId = iconResId;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public int getIconResId() {
        return iconResId;
    }
}
