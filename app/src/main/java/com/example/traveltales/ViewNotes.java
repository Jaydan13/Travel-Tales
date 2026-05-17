package com.example.traveltales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewNotes extends AppCompatActivity {

    ImageButton backBtn;
    TextView viewCountryName, viewFromDate, viewToDate, viewDurationNo, viewSpinnerDuration;
    ImageView viewCountryFlagImage;
    RecyclerView viewNotesRecycler;
    Button editBtn, deleteBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_notes);

        ThemeHelper.applyTheme(this);

        backBtn = findViewById(R.id.backBtn);

        viewCountryName = findViewById(R.id.viewCountryName);
        viewFromDate = findViewById(R.id.viewFromDate);
        viewToDate = findViewById(R.id.viewToDate);
        viewDurationNo = findViewById(R.id.viewDurationNo);
        viewSpinnerDuration = findViewById(R.id.viewSpinnerDuration);
        viewCountryFlagImage = findViewById(R.id.viewCountryFlagImage);

        viewNotesRecycler = findViewById(R.id.viewNotesRecycler);

        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String id = getIntent().getStringExtra("id");
        String countryName = getIntent().getStringExtra("countryName");
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String durationNumber = getIntent().getStringExtra("durationNumber");
        String durationPeriod = getIntent().getStringExtra("durationPeriod");
        String fromDate = getIntent().getStringExtra("fromDate");
        String toDate = getIntent().getStringExtra("toDate");
        List<Note> notesList = (List<Note>) getIntent().getSerializableExtra("notes");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewNotes.this, HomePage.class);
                startActivity(intent);
            }
        });

        viewCountryName.setText(countryName);
        viewDurationNo.setText(durationNumber);
        viewSpinnerDuration.setText(durationPeriod);
        viewFromDate.setText(fromDate);
        viewToDate.setText(toDate);
        if (imageUrl == null || imageUrl.isEmpty()) {
            viewCountryFlagImage.setImageResource(R.drawable.photo);
        } else {
            Glide.with(this).load(imageUrl).into(viewCountryFlagImage);
        }
        NotesAdapter adapter = new NotesAdapter(notesList);

        viewNotesRecycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        viewNotesRecycler.setAdapter(adapter);


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ViewNotes.this, EditNotes.class);

                intent.putExtra("id", id);
                intent.putExtra("countryName", countryName);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("durationNumber", durationNumber);
                intent.putExtra("durationPeriod", durationPeriod);
                intent.putExtra("fromDate", fromDate);
                intent.putExtra("toDate", toDate);
                intent.putExtra("notes", (java.io.Serializable) notesList);

                startActivity(intent);
            }
        });

        deleteBtn.setOnClickListener(v -> {
            String userId = mAuth.getCurrentUser().getUid();

            db.collection("users").document(userId).collection("notes").document(id).delete().addOnSuccessListener(unused -> {
                Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ViewNotes.this, HomePage.class);
                startActivity(intent);
            });
        });
    }
}