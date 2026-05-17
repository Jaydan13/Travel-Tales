package com.example.traveltales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<Note> notesList;
    private boolean useCustomLayout;

    public NotesAdapter(List<Note> notesList) {
        this.notesList = notesList;
        this.useCustomLayout = useCustomLayout;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, note;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.noteTitle);
            note = itemView.findViewById(R.id.noteText);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_note, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Note note = notesList.get(position);

        holder.title.setText(note.getTitle());
        holder.note.setText(note.getNote());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}