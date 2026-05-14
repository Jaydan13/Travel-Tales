package com.example.traveltales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    ArrayList<Note> notesList;

    public NotesAdapter(ArrayList<Note> notesList) {
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Note note = notesList.get(position);

        holder.dayText.setText(note.getTitle());
        holder.noteText.setText(note.getNote());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayText;
        TextView noteText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dayText = itemView.findViewById(android.R.id.text1);
            noteText = itemView.findViewById(android.R.id.text2);
        }
    }
}