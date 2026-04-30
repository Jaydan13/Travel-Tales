package com.example.traveltales;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class EditNotes extends AppCompatActivity {

    ImageButton backBtn;
    EditText editCountryName, editDurationNo;
    Button editFromDate, editToDate, saveBtn;
    String fromDateSelected = "", toDateSelected = "";
    Spinner editSpinnerDuration;
    RecyclerView editNotesRecycler;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_notes);

        backBtn = findViewById(R.id.backBtn);

        editCountryName = findViewById(R.id.editCountryName);
        editDurationNo = findViewById(R.id.editDurationNo);

        editFromDate = findViewById(R.id.editFromDate);
        editToDate = findViewById(R.id.editToDate);
        saveBtn = findViewById(R.id.saveBtn);

        editSpinnerDuration = findViewById(R.id.editSpinnerDuration);

        editNotesRecycler = findViewById(R.id.editNotesRecycler);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String[] durationOptions = {"Days", "Weeks", "Months"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSpinnerDuration.setAdapter(durationAdapter);

        editFromDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                month = month +1;
                fromDateSelected = dayOfMonth + "/" + month + "/" + year;
                editFromDate.setText(fromDateSelected);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
        });

        editToDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                month = month +1;
                toDateSelected = dayOfMonth + "/" + month + "/" + year;
                editToDate.setText(toDateSelected);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            datePicker.show();
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditNotes.this, HomePage.class);
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(view -> updateNotes());

    }
    private void updateNotes() {
        String name = editCountryName.getText().toString().trim();
        String durationNumberStr = editDurationNo.getText().toString().trim();
        String durationPeriod = editSpinnerDuration.getSelectedItem().toString();
    }
}