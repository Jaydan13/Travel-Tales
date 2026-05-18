package com.example.traveltales;

import java.io.Serializable;

public class Note implements Serializable {

    private String title;
    private String note;

    public Note() {

    }

    public Note(String title, String note) {
        this.title = title;
        this.note = note;
    }

    public String getTitle() {return title;}
    public String getNote() {return note;}

}