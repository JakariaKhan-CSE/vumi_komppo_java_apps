package com.munna.earthquake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EarthquakeApp";
    private static final String USGS_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson";

    private RecyclerView recyclerView;
    private EarthquakeAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Earthquake> earthquakeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EarthquakeAdapter(this, earthquakeList);
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this::fetchEarthquakes);

        Button btnFetch = findViewById(R.id.btnFetch);
        btnFetch.setOnClickListener(v -> fetchEarthquakes());

        FloatingActionButton fabMap = findViewById(R.id.fabMap);
        fabMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            intent.putExtra("earthquakeList", earthquakeList);
            startActivity(intent);
        });

        FloatingActionButton fabSettings = findViewById(R.id.fabSettings);
        fabSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        fetchEarthquakes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list when returning from settings to apply filters
        if (!earthquakeList.isEmpty()) {
             filterEarthquakes();
        }
    }

    private void fetchEarthquakes() {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            String jsonResponse = null;
            try {
                jsonResponse = makeHttpRequest(new URL(USGS_URL));
            } catch (IOException e) {
                Log.e(TAG, "Error fetching data", e);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
                return;
            }

            if (jsonResponse == null) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "No response from server", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
                return;
            }

            final List<Earthquake> parsedList = parseEarthquakes(jsonResponse);

            runOnUiThread(() -> {
                earthquakeList.clear();
                earthquakeList.addAll(parsedList);
                filterEarthquakes();
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }

    private void filterEarthquakes() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagStr = prefs.getString("min_magnitude", "1.0");
        double minMag = 1.0;
        try {
            minMag = Double.parseDouble(minMagStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid min magnitude", e);
        }

        List<Earthquake> filteredList = new ArrayList<>();
        for (Earthquake eq : earthquakeList) {
            if (eq.getMagnitude() >= minMag) {
                filteredList.add(eq);
            }
        }
        adapter.updateData(filteredList);
    }

    private String makeHttpRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        StringBuilder output = new StringBuilder();

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (inputStream != null) inputStream.close();
        }
        return output.toString();
    }

    private List<Earthquake> parseEarthquakes(String jsonResponse) {
        List<Earthquake> earthquakes = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray features = root.getJSONArray("features");

            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");

                double magnitude = properties.optDouble("mag", 0.0);
                String place = properties.optString("place", "Unknown location");
                long time = properties.optLong("time", 0L);
                String url = properties.optString("url", "");
                
                double longitude = coordinates.optDouble(0, 0.0);
                double latitude = coordinates.optDouble(1, 0.0);
                double depth = coordinates.optDouble(2, 0.0);

                earthquakes.add(new Earthquake(magnitude, place, time, url, latitude, longitude, depth));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
        return earthquakes;
    }
}
