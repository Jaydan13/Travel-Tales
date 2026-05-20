package com.example.traveltales;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ThemeHelper {

    public static void applyTheme(Activity activity) {

        int colour = activity.getSharedPreferences("SettingsPrefs", Context.MODE_PRIVATE)
                .getInt("appColourValue", ContextCompat.getColor(activity, R.color.blue));

        View root = activity.findViewById(android.R.id.content);
        applyToViews(root, colour);
    }

    private static void applyToViews(View view, int colour) {

        Object tag = view.getTag();

        // Only apply if tag = "appTheme"
        if ("appTheme".equals(tag)) {

            // BUTTONS → change background tint
            if (view instanceof Button) {
                Button btn = (Button) view;
                btn.setBackgroundTintList(ColorStateList.valueOf(colour));
            } else if (view instanceof LinearLayout) { // LAYOUTS → change background
                view.setBackgroundColor(colour);
            } else if (view instanceof FloatingActionButton) {
                FloatingActionButton fabBtn = (FloatingActionButton) view;
                fabBtn.setBackgroundTintList(ColorStateList.valueOf(colour));
            }
        }
        // Loop through children
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyToViews(group.getChildAt(i), colour);
            }
        }
    }
}
