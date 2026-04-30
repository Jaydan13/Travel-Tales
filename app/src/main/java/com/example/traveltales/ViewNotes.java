package com.example.traveltales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewNotes extends AppCompatActivity {

    ImageButton backBtn;
    TextView viewCountryName, viewFromDate, viewToDate, viewDurationNo, viewSpinnerDuration;
    RecyclerView viewNotesRecycler;
    Button editBtn, deleteBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_notes);

        backBtn = findViewById(R.id.backBtn);

        viewCountryName = findViewById(R.id.viewCountryName);
        viewFromDate = findViewById(R.id.viewFromDate);
        viewToDate = findViewById(R.id.viewToDate);
        viewDurationNo = findViewById(R.id.viewDurationNo);
        viewSpinnerDuration = findViewById(R.id.viewSpinnerDuration);

        viewNotesRecycler = findViewById(R.id.viewNotesRecycler);

        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewNotes.this, HomePage.class);
                startActivity(intent);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewNotes.this, EditNotes.class);
                startActivity(intent);
            }
        });
    }
}