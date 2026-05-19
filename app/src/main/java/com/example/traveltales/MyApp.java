package com.example.traveltales;

import android.app.Application;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dmllbhsrc");
        config.put("api_key", "119362629813927");
        config.put("api_secret", "6PpRbRgj4J2kXmwX6MJUs3QzOAY");

        MediaManager.init(this, config);
    }
}