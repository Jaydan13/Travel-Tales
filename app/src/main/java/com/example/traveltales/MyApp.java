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
        config.put("cloud_name", "YOUR_CLOUD_NAME");
        config.put("api_key", "YOUR_API_KEY");
        config.put("api_secret", "YOUR_API_SECRET");

        MediaManager.init(this, config);
    }
}