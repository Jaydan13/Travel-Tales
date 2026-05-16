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

public class HomePage extends AppCompatActivity {

    Button addBtn;
    ImageButton profileBtn;
    RecyclerView notesRecycler;
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

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Profile.class);
                startActivity(intent);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, AddNotes.class);
                startActivity(intent);
            }
        });

    }
}