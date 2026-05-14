package com.example.traveltales;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<NoteEntry> notesList;

    public NotesAdapter(List<NoteEntry> notesList) {
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note_entry, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NoteEntry entry = notesList.get(position);

        holder.dayTitle.setText(entry.getTitle());

        // Remove old watcher safely
        if (holder.noteEditText.getTag() instanceof TextWatcher) {
            holder.noteEditText.removeTextChangedListener(
                    (TextWatcher) holder.noteEditText.getTag()
            );
        }

        holder.noteEditText.setText(entry.getNotes() != null ? entry.getNotes() : "");

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                entry.setNotes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        holder.noteEditText.addTextChangedListener(watcher);
        holder.noteEditText.setTag(watcher);

        holder.locationBtn.setOnClickListener(v -> {});

        holder.uploadBtn.setOnClickListener(v -> {});
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayTitle;
        EditText noteEditText;
        Button locationBtn, uploadBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            dayTitle = itemView.findViewById(R.id.dayTitle);
            noteEditText = itemView.findViewById(R.id.noteEditText);
            locationBtn = itemView.findViewById(R.id.locationBtn);
            uploadBtn = itemView.findViewById(R.id.uploadBtn);
        }
    }
}