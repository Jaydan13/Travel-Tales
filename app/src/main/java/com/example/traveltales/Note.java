package com.example.traveltales;

public class Note {

    private String title;
    private String note;
    private String location;
    private String imageUri;

    public Note(String title, String note) {
        this.title = title;
        this.note = note;
    }

    public String getTitle() {return title;}

    public String getNote() {return note;}
}