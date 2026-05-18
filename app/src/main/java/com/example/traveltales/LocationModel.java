package com.example.traveltales;

import java.io.Serializable;

public class LocationModel implements Serializable {

    private String title;
    private double latitude;
    private double longitude;

    // Empty constructor required for Firestore
    public LocationModel() {
    }

    public LocationModel(String title, double latitude, double longitude) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public String getTitle() {return title;}
    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}
    // Setters
    public void setTitle(String title) {this.title = title;}
    public void setLatitude(double latitude) {this.latitude = latitude;}
    public void setLongitude(double longitude) {this.longitude = longitude;}
}