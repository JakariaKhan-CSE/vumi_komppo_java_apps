package com.munna.earthquake;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);

        // Load osmdroid configuration
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_map);

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        Earthquake earthquake = (Earthquake) getIntent().getSerializableExtra("earthquake");
        ArrayList<Earthquake> earthquakeList = (ArrayList<Earthquake>) getIntent().getSerializableExtra("earthquakeList");

        if (earthquake != null) {
            // Show single earthquake
            GeoPoint startPoint = new GeoPoint(earthquake.getLatitude(), earthquake.getLongitude());
            map.getController().setZoom(10.0);
            map.getController().setCenter(startPoint);
            addMarker(earthquake);
        } else if (earthquakeList != null) {
            // Show all earthquakes
            if (!earthquakeList.isEmpty()) {
                GeoPoint startPoint = new GeoPoint(earthquakeList.get(0).getLatitude(), earthquakeList.get(0).getLongitude());
                map.getController().setZoom(4.0);
                map.getController().setCenter(startPoint);
                for (Earthquake eq : earthquakeList) {
                    addMarker(eq);
                }
            }
        } else {
             // Default view
            GeoPoint startPoint = new GeoPoint(0.0, 0.0);
            map.getController().setZoom(2.0);
            map.getController().setCenter(startPoint);
        }
    }

    private void addMarker(Earthquake earthquake) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(earthquake.getLatitude(), earthquake.getLongitude()));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(earthquake.getLocation());
        marker.setSnippet("Mag: " + earthquake.getMagnitude());
        map.getOverlays().add(marker);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
