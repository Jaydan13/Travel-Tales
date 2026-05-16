package com.example.traveltales;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisitListAdapter extends RecyclerView.Adapter<VisitListAdapter.ViewHolder> {

    private List<VisitItem> visitList;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public VisitListAdapter(List<VisitItem> visitList) {
        this.visitList = visitList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView visitCountryName;
        ImageButton visitedBtn, removeBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            visitCountryName = itemView.findViewById(R.id.toVisitName);
            visitedBtn = itemView.findViewById(R.id.visitedBtn);
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_visit_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VisitItem item = visitList.get(position);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        holder.itemView.setBackgroundResource(R.drawable.edittextborder);
        holder.visitCountryName.setText(item.getVisitCountryName());

        holder.visitedBtn.setOnClickListener(v -> {
            Context context = v.getContext();
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.create_note);

            Button yesBtn = dialog.findViewById(R.id.yesBtn);
            Button laterBtn = dialog.findViewById(R.id.laterBtn);

            String userId = mAuth.getCurrentUser().getUid();

            yesBtn.setOnClickListener(view -> {

                db.collection("users").document(userId).collection("visitList").document(item.getId()).delete().addOnSuccessListener(unused -> {

                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        visitList.remove(pos);
                        notifyItemRemoved(pos);
                    }

                    // open AddNotes
                    Intent intent = new Intent(context, AddNotes.class);
                    intent.putExtra("countryName", item.getVisitCountryName());
                    context.startActivity(intent);

                    dialog.dismiss();
                });
            });

            laterBtn.setOnClickListener(view -> {

                Map<String, Object> noteData = new HashMap<>();
                noteData.put("countryName", item.getVisitCountryName());

                // Save country only
                db.collection("users").document(userId).collection("notes").add(noteData).addOnSuccessListener(documentReference -> {

                    db.collection("users").document(userId).collection("visitList").document(item.getId()).delete();

                    // Remove from RecyclerView
                    int currentPosition = holder.getAdapterPosition();

                    if (currentPosition != RecyclerView.NO_POSITION) {
                        visitList.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                    }

                    Toast.makeText(context, "Country moved to notes", Toast.LENGTH_SHORT).show();

                    dialog.dismiss();
                });
            });
        });

        holder.removeBtn.setOnClickListener(v -> {
            Context context = v.getContext();
            String userId = mAuth.getCurrentUser().getUid();
            db.collection("users").document(userId).collection("visitList").document(item.getId()).delete().addOnSuccessListener(unused -> {
                int currentPosition = holder.getAdapterPosition();

                if (currentPosition != RecyclerView.NO_POSITION) {
                    visitList.remove(currentPosition);
                    notifyItemRemoved(currentPosition);
                }
                Toast.makeText(context, "Country removed", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to remove", Toast.LENGTH_SHORT).show();
            });
        });
    }
    @Override
    public int getItemCount() {
        return visitList.size();
    }
}
