package com.example.heatstrokealertapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private String[] dates;
    private float[] temperatures;
    private int[] weatherIcons;

    // Constructor to accept data
    public WeatherAdapter(String[] dates, float[] temperatures, int[] weatherIcons) {
        this.dates = dates;
        this.temperatures = temperatures;
        this.weatherIcons = weatherIcons;
    }

    // ViewHolder class to hold the views for each item
    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        public TextView dateText, temperatureText;
        public ImageView weatherIcon;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            temperatureText = itemView.findViewById(R.id.temperatureText);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
        }
    }

    @Override
    public WeatherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout and create the ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_item, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WeatherViewHolder holder, int position) {
        // Set the data for each item
        holder.dateText.setText(dates[position]);
        holder.temperatureText.setText(String.format("%s°C", temperatures[position]));
        holder.weatherIcon.setImageResource(weatherIcons[position]); // Use your weather icon resource
    }

    @Override
    public int getItemCount() {
        return dates.length;  // Number of items (days in the forecast)
    }
}
