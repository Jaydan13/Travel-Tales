package com.example.traveltales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private List<LocationModel> locationList;

    public LocationAdapter(List<LocationModel> locationList) {
        this.locationList = locationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LocationModel location = locationList.get(position);

        holder.locationTitleTxt.setText(location.getTitle());
        String latLng = "Lat: " + location.getLatitude() + "\nLng: " + location.getLongitude();
        holder.locationLatLngTxt.setText(latLng);
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView locationTitleTxt;
        TextView locationLatLngTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            locationTitleTxt = itemView.findViewById(R.id.locationTitleTxt);
            locationLatLngTxt = itemView.findViewById(R.id.locationLatLngTxt);
        }
    }
}