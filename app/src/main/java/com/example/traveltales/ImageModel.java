package com.example.traveltales;

import java.io.Serializable;

public class ImageModel implements Serializable {

    private String imageUri;

    public ImageModel(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageUri() {return imageUri;}
}