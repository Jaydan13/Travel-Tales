package com.example.traveltales;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddNotes extends AppCompatActivity {

    Button backBtn, saveBtn;
    EditText countryName, fromDate, toDate, notesText;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_notes);

        backBtn = findViewById(R.id.backBtn);
        saveBtn = findViewById(R.id.saveBtn);
        countryName = findViewById(R.id.countryName);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        notesText = findViewById(R.id.notesText);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddNotes.this, HomePage.class);
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(view -> saveData());

    }

    private void saveData() {
        String name = countryName.getText().toString().trim();
        String notes = notesText.getText().toString().trim();
        String dateFrom = fromDate.getText().toString().trim();
        String dateTo = toDate.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(notes) || TextUtils.isEmpty(dateFrom) || TextUtils.isEmpty(dateTo)) {
            Toast.makeText(this, "Fill all fields!!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}