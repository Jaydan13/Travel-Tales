package com.example.traveltales;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

public class LocationMapDialog extends DialogFragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String title;

    public LocationMapDialog(double latitude, double longitude, String title) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_location_map);

        Button backBtn = dialog.findViewById(R.id.backBtn);
        Button zoomInBtn = dialog.findViewById(R.id.zoomInBtn);
        Button zoomOutBtn = dialog.findViewById(R.id.zoomOutBtn);

        zoomInBtn.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        zoomOutBtn.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });

        backBtn.setOnClickListener(v -> dismiss());

        SupportMapFragment mapFragment = new SupportMapFragment();

        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return dialog;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        LatLng location = new LatLng(latitude, longitude);

        mMap.addMarker(new MarkerOptions().position(location).title(title));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    }
}