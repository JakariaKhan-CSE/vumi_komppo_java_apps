package com.munna.earthquake;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Earthquake earthquake = (Earthquake) getIntent().getSerializableExtra("earthquake");

        if (earthquake == null) {
            finish();
            return;
        }

        TextView txtMagnitude = findViewById(R.id.txtDetailsMagnitude);
        TextView txtLocation = findViewById(R.id.txtDetailsLocation);
        TextView txtTime = findViewById(R.id.txtDetailsTime);
        TextView txtCoordinates = findViewById(R.id.txtCoordinates);
        TextView txtDepth = findViewById(R.id.txtDepth);
        Button btnViewOnMap = findViewById(R.id.btnViewOnMap);
        Button btnMoreInfo = findViewById(R.id.btnMoreInfo);

        txtMagnitude.setText(String.format(Locale.getDefault(), "%.1f", earthquake.getMagnitude()));
        txtLocation.setText(earthquake.getLocation());

        Date dateObject = new Date(earthquake.getTime());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        txtTime.setText(dateFormatter.format(dateObject));

        txtCoordinates.setText(String.format(Locale.getDefault(), "%.4f, %.4f", earthquake.getLatitude(), earthquake.getLongitude()));
        txtDepth.setText(String.format(Locale.getDefault(), "%.1f km", earthquake.getDepth()));

        // Set magnitude color
        GradientDrawable magnitudeCircle = (GradientDrawable) txtMagnitude.getBackground();
        magnitudeCircle.setColor(getMagnitudeColor(earthquake.getMagnitude()));

        btnViewOnMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("earthquake", earthquake);
            startActivity(intent);
        });

        btnMoreInfo.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(earthquake.getUrl()));
            startActivity(i);
        });
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude_low;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude_low;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude_medium;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude_medium;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude_high;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude_high;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude_high;
                break;
        }
        return ContextCompat.getColor(this, magnitudeColorResourceId);
    }
}
