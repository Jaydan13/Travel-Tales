package com.example.traveltales;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HomeNotesAdapter extends RecyclerView.Adapter<HomeNotesAdapter.ViewHolder> {

    private Context context;
    private List<HomeNoteItem> noteList;

    public HomeNotesAdapter(Context context, List<HomeNoteItem> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView homeCountryFlag;
        TextView homeCountryName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            homeCountryFlag = itemView.findViewById(R.id.homeCountryFlag);
            homeCountryName = itemView.findViewById(R.id.homeCountryName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.home_notes_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        HomeNoteItem item = noteList.get(position);

        holder.homeCountryName.setText(item.getCountryName());

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(context).load(item.getImageUrl()).into(holder.homeCountryFlag);
        } else {
            holder.homeCountryFlag.setImageResource(R.drawable.photo);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewNotes.class);

            intent.putExtra("id", item.getId());
            intent.putExtra("countryName", item.getCountryName());
            intent.putExtra("imageUrl", item.getImageUrl());
            intent.putExtra("durationNumber", item.getDurationNumber());
            intent.putExtra("durationPeriod", item.getDurationPeriod());
            intent.putExtra("fromDate", item.getFromDate());
            intent.putExtra("toDate", item.getToDate());
            intent.putExtra("notes", (java.io.Serializable) item.getNotes());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}