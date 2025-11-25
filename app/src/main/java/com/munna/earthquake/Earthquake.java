package com.munna.earthquake;

import java.io.Serializable;

public class Earthquake implements Serializable {
    private double magnitude;
    private String location;
    private long time;
    private String url;
    private double latitude;
    private double longitude;
    private double depth;

    public Earthquake(double magnitude, String location, long time, String url, double latitude, double longitude, double depth) {
        this.magnitude = magnitude;
        this.location = location;
        this.time = time;
        this.url = url;
        this.latitude = latitude;
        this.longitude = longitude;
        this.depth = depth;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public long getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDepth() {
        return depth;
    }
}
