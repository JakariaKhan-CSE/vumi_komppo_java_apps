package com.munna.earthquake;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.EarthquakeViewHolder> {

    private List<Earthquake> earthquakes;
    private Context context;

    public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
        this.context = context;
        this.earthquakes = earthquakes;
    }

    @NonNull
    @Override
    public EarthquakeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_earthquake, parent, false);
        return new EarthquakeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EarthquakeViewHolder holder, int position) {
        Earthquake earthquake = earthquakes.get(position);

        holder.txtMagnitude.setText(String.format(Locale.getDefault(), "%.1f", earthquake.getMagnitude()));
        holder.txtLocation.setText(earthquake.getLocation());

        Date dateObject = new Date(earthquake.getTime());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault());
        holder.txtTime.setText(dateFormatter.format(dateObject));

        // Set magnitude color
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.txtMagnitude.getBackground();
        int magnitudeColor = getMagnitudeColor(earthquake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            intent.putExtra("earthquake", earthquake);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return earthquakes.size();
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
        return ContextCompat.getColor(context, magnitudeColorResourceId);
    }

    public void updateData(List<Earthquake> newEarthquakes) {
        this.earthquakes = newEarthquakes;
        notifyDataSetChanged();
    }

    static class EarthquakeViewHolder extends RecyclerView.ViewHolder {
        TextView txtMagnitude;
        TextView txtLocation;
        TextView txtTime;

        public EarthquakeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMagnitude = itemView.findViewById(R.id.txtMagnitude);
            txtLocation = itemView.findViewById(R.id.txtLocation);
            txtTime = itemView.findViewById(R.id.txtTime);
        }
    }
}
