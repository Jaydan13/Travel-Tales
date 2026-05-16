package com.example.traveltales;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ToVisit extends AppCompatActivity {

    ImageButton backBtn;
    RecyclerView toVisitRecycler;
    Button addVisitBtn;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_to_visit);

        backBtn = findViewById(R.id.backBtn);
        addVisitBtn = findViewById(R.id.addVisitBtn);

        toVisitRecycler = findViewById(R.id.toVisitRecycler);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToVisit.this, Profile.class);
                startActivity(intent);
            }
        });

        addVisitBtn.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.add_visit_list);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            EditText visitCountry = dialog.findViewById(R.id.visitCountry);
            Button addToListBtn = dialog.findViewById(R.id.addToListBtn);

            addToListBtn.setOnClickListener(view -> {
                String visitCountryName = visitCountry.getText().toString().trim();
            });
        });

    }
}