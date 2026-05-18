package com.example.traveltales;

import android.app.Dialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.io.IOException;
import java.util.List;

public class MapPickerDialog extends DialogFragment
        implements OnMapReadyCallback {

    private GoogleMap mMap;

    private double selectedLat;
    private double selectedLng;
    private String selectedTitle = "";

    private OnLocationSelectedListener listener;

    public interface OnLocationSelectedListener {
        void onLocationSelected(LocationModel location);
    }

    public void setOnLocationSelectedListener(OnLocationSelectedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_map_picker);

        SearchView searchView = dialog.findViewById(R.id.locationSearchView);
        Button backBtn = dialog.findViewById(R.id.mapBackBtn);
        Button saveBtn = dialog.findViewById(R.id.mapSaveBtn);
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

        SupportMapFragment mapFragment = new SupportMapFragment();

        getChildFragmentManager().beginTransaction().replace(R.id.mapContainer, mapFragment).commit();

        mapFragment.getMapAsync(this);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // SEARCH
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Geocoder geocoder = new Geocoder(requireContext());

                try {

                    List<Address> addressList = geocoder.getFromLocationName(query, 1);

                    if (!addressList.isEmpty()) {

                        Address address = addressList.get(0);

                        selectedLat = address.getLatitude();
                        selectedLng = address.getLongitude();
                        selectedTitle = query;

                        LatLng latLng = new LatLng(selectedLat, selectedLng);

                        if (mMap != null) {

                            mMap.clear();

                            mMap.addMarker(new MarkerOptions().position(latLng).title(query));

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // BACK BUTTON
        backBtn.setOnClickListener(v -> dismiss());

        // SAVE BUTTON
        saveBtn.setOnClickListener(v -> {

            LocationModel location = new LocationModel(selectedTitle, selectedLat, selectedLng);

            if (listener != null) {
                listener.onLocationSelected(location);
            }

            dismiss();
        });

        return dialog;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        LatLng london = new LatLng(51.5074, -0.1278);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 10));
    }
}