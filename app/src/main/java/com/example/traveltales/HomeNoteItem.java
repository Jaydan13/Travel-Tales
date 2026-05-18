package com.example.traveltales;

import java.util.List;

public class HomeNoteItem {

    private String id;
    private String countryName;
    private String imageUrl;
    private String durationNumber;
    private String durationPeriod;
    private String fromDate;
    private String toDate;
    private List<Note> notes;
    private List<LocationModel> locations;

    public HomeNoteItem(String id, String countryName, String imageUrl, String durationNumber, String durationPeriod, String fromDate, String toDate, List<Note> notes, List<LocationModel> locations) {
        this.id = id;
        this.countryName = countryName;
        this.imageUrl = imageUrl;
        this.durationNumber = durationNumber;
        this.durationPeriod = durationPeriod;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.notes = notes;
        this.locations = locations;
    }

    public String getId() {return id;}
    public String getCountryName() {return countryName;}
    public String getImageUrl() {return imageUrl;}
    public String getDurationNumber() {return durationNumber;}
    public String getDurationPeriod() {return durationPeriod;}
    public String getFromDate() {return fromDate;}
    public String getToDate() {return toDate;}
    public List<Note> getNotes() {return notes;}
    public List<LocationModel> getLocations() {return locations;}
}
