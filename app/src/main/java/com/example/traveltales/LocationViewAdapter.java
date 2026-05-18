package com.example.traveltales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewAdapter.ViewHolder> {

    private List<LocationModel> locationList;

    public LocationViewAdapter(List<LocationModel> locationList) {
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

        holder.title.setText(location.getTitle());
        String latLng = "Lat: " + location.getLatitude() + "\nLng: " + location.getLongitude();
        holder.latLng.setText(latLng);

        holder.itemView.setOnClickListener(v -> {

            LocationMapDialog dialog = new LocationMapDialog(location.getLatitude(), location.getLongitude(), location.getTitle());

            dialog.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "location_map");
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, latLng;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.locationTitleTxt);
            latLng = itemView.findViewById(R.id.locationLatLngTxt);
        }
    }
}