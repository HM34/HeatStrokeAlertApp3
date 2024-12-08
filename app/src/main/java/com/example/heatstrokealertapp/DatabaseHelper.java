package com.example.heatstrokealertapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cities.db"; // Database name
    private static final int DATABASE_VERSION = 112; // Database version

    private static final String TABLE_CITIES = "cities"; // Table name
    private static final String COLUMN_ID = "id"; // Column for ID
    private static final String COLUMN_NAME = "city_name"; // Column for city name

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the cities table if it doesn't exist
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CITIES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT)";
        db.execSQL(CREATE_TABLE);

        // Insert cities into the database
        insertCities(db);
    }

    // Insert cities into the database (simplified)
    private void insertCities(SQLiteDatabase db) {
        String[] cities = {
                "Dubai", "Abu Dhabi", "Sharjah", "Doha", "Muscat", "Manama",
                "Alexandria", "Sanaa", "Khartoum", "Tunis", "Algiers", "Casablanca",
                "Cairo", "Tehran", "Baku", "Yerevan", "Tbilisi", "Kuwait City",
                "Kazan", "Nizhny Novgorod", "Rostov-on-Don", "Minsk", "Belgrade", "Zagreb",
                "Ljubljana", "Zagreb", "Sarajevo", "Belgrade", "Athens", "London",
                "Paris", "Berlin", "Rome", "Madrid", "Oslo", "Stockholm",
                "Edinburgh", "Glasgow", "Manchester", "Birmingham", "Leeds", "Liverpool",
                "Naples", "Turin", "Florence", "Milan", "Venice", "Bologna", "Genoa",
                "Lyon", "Marseille", "Nice", "Toulouse", "Strasbourg", "Berlin",
                "Auckland", "Wellington", "New York", "Los Angeles", "Chicago",
                "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego",
                "Dallas", "San Francisco", "Seattle", "Denver", "Austin", "Fort Worth",
                "Portland", "Las Vegas", "Louisville", "Milwaukee", "Kansas City",
                "Oklahoma City", "Tucson", "Albuquerque", "Mesa", "Atlanta", "Miami",
                "Riyadh", "Jeddah", "Mecca", "Medina", "Khobar", "Dammam",
                "Abha", "Khamis Mushait", "Tabuk", "Buraidah", "Najran", "Hail",
                "Jubail", "Al Khobar", "Yanbu", "Al Qassim", "Al-Ahsa", "Jizan",
                "Al-Dawadmi", "Al-Rass", "Al Qunfudhah", "Shaqra", "Mekkah",
                "Medina", "Rafha", "Wadi ad-Dawasir", "Sakakah", "Al-Faisaliyah",
                "Dharan", "Al-Laith", "Al-Jouf", "Qatif", "Madinah", "Turaif", "Riyadh Province"
        };

        db.beginTransaction(); // Start transaction for better performance
        try {
            for (String city : cities) {
                String insertQuery = "INSERT INTO " + TABLE_CITIES + " (" + COLUMN_NAME + ") VALUES (?)";
                db.execSQL(insertQuery, new Object[]{city});
                Log.d("DatabaseHelper", "Inserted city: " + city);
            }
            db.setTransactionSuccessful(); // Mark transaction as successful
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting cities", e);
        } finally {
            db.endTransaction(); // End transaction
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES); // Drop old table if exists
        onCreate(db); // Recreate the table
    }

    // Fetch all cities (simplified without any search query)
    public ArrayList<String> getCities(String query) {
        ArrayList<String> cityList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + COLUMN_NAME + " FROM " + TABLE_CITIES;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(COLUMN_NAME);
            while (!cursor.isAfterLast()) {
                String city = cursor.getString(columnIndex);
                cityList.add(city);
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            Log.d("DatabaseHelper", "No cities found.");
        }
        db.close();
        return cityList;
    }
}
