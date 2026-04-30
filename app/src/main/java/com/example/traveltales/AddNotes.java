package com.example.traveltales;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AddNotes extends AppCompatActivity {

    Button saveBtn, fromDate, toDate;
    String fromDateSelected = "", toDateSelected = "";
    ImageButton backBtn;
    EditText countryName, durationNo;
    Spinner spinnerDuration;
    RecyclerView addNotesRecycler;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_notes);

        backBtn = findViewById(R.id.backBtn);
        saveBtn = findViewById(R.id.saveBtn);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);

        countryName = findViewById(R.id.countryName);
        durationNo = findViewById(R.id.durationNo);

        spinnerDuration = findViewById(R.id.spinnerDuration);

        addNotesRecycler = findViewById(R.id.addNotesRecycler);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String[] durationOptions = {"Days", "Weeks", "Months"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddNotes.this, HomePage.class);
                startActivity(intent);
            }
        });

        fromDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                month = month +1;
                fromDateSelected = dayOfMonth + "/" + month + "/" + year;
                fromDate.setText(fromDateSelected);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
        });

        toDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                month = month +1;
                toDateSelected = dayOfMonth + "/" + month + "/" + year;
                toDate.setText(toDateSelected);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
        });

        saveBtn.setOnClickListener(view -> saveData());

    }

    private void saveData() {
        String name = countryName.getText().toString().trim();
        String durationNumberStr = durationNo.getText().toString().trim();
        String durationPeriod = spinnerDuration.getSelectedItem().toString();
    }
}