package com.example.traveltales;

public class VisitItem {

    String id;
    private String visitCountryName, visitCountryNameLower;

    public VisitItem() {

    }

    public VisitItem(String id, String visitCountryName, String visitCountryNameLower) {
        this.id = id;
        this.visitCountryName = visitCountryName;
        this.visitCountryNameLower = visitCountryNameLower;
    }

    public String getId() {return id;}
    public String getVisitCountryName() {return visitCountryName;}
    public String getVisitCountryNameLower() {return visitCountryNameLower;}
}
