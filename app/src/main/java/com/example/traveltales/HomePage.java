package com.example.traveltales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    Button addBtn;
    ImageButton profileBtn;
    RecyclerView notesRecycler;
    List<HomeNoteItem> noteList;
    HomeNotesAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);

        ThemeHelper.applyTheme(this);

        profileBtn = findViewById(R.id.profileBtn);
        addBtn = findViewById(R.id.addBtn);
        notesRecycler = findViewById(R.id.notesRecycler);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        noteList = new ArrayList<>();
        adapter = new HomeNotesAdapter(this, noteList);
        notesRecycler.setAdapter(adapter);
        notesRecycler.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Profile.class);
                startActivity(intent);
            }
        });

        loadNotes();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, AddNotes.class);
                startActivity(intent);
            }
        });

    }
    protected void onResume() {
        super.onResume();
        loadNotes();
    }
    private void loadNotes() {

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("notes").get().addOnSuccessListener(queryDocumentSnapshots -> {

            noteList.clear();

            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {

                String id = doc.getId();
                String countryName = doc.getString("countryName");
                String imageUrl = doc.getString("imageUrl");
                String durationNumber = doc.getString("durationNumber");
                String durationPeriod = doc.getString("durationPeriod");
                String fromDate = doc.getString("fromDate");
                String toDate = doc.getString("toDate");
                List<Note> notesList = new ArrayList<>();
                List<LocationModel> locationList = new ArrayList<>();

                List<Map<String, Object>> rawNotes = (List<Map<String, Object>>) doc.get("notes");

                if (rawNotes != null) {
                    for (Map<String, Object> item : rawNotes) {

                        String title = (String) item.get("title");
                        String noteText = (String) item.get("note");

                        notesList.add(new Note(title, noteText));
                    }
                }

                List<Map<String, Object>> rawLocations = (List<Map<String, Object>>) doc.get("locations");
                if (rawLocations != null) {
                    for (Map<String, Object> item : rawLocations) {

                        String title = (String) item.get("title");
                        double latitude = 0;
                        double longitude = 0;

                        if (item.get("latitude") != null) {
                            latitude = ((Number) item.get("latitude")).doubleValue();
                        }
                        if (item.get("longitude") != null) {
                            longitude = ((Number) item.get("longitude")).doubleValue();
                        }
                        locationList.add(new LocationModel(title, latitude, longitude));
                    }
                }

                noteList.add(new HomeNoteItem(id, countryName, imageUrl, durationNumber, durationPeriod, fromDate, toDate, notesList, locationList));
            }

            adapter.notifyDataSetChanged();
        });
    }
}