package com.example.traveltales;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddNotes extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    Button addCountryFlagChooseBtn, saveBtn, fromDate, toDate;
    ImageView addCountryFlagImage;
    String fromDateSelected = "", toDateSelected = "";
    ImageButton backBtn;
    EditText countryName, durationNo;
    Spinner spinnerDuration;
    List<NoteEntry> noteEntries;
    NotesAdapter adapter;
    RecyclerView addNotesRecycler;
    Uri imageUri;
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
        addCountryFlagChooseBtn = findViewById(R.id.addCountryFlagChooseBtn);

        addCountryFlagImage = findViewById(R.id.addCountryFlagImage);

        countryName = findViewById(R.id.countryName);
        durationNo = findViewById(R.id.durationNo);

        spinnerDuration = findViewById(R.id.spinnerDuration);

        addNotesRecycler = findViewById(R.id.addNotesRecycler);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        durationNo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override public void afterTextChanged(Editable s) {
                generateNoteEntries();
            }
        });

        String[] durationOptions = {"Days", "Weeks", "Months"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);

        noteEntries = new ArrayList<>();
        addNotesRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotesAdapter(noteEntries);
        addNotesRecycler.setAdapter(adapter);

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

        addCountryFlagChooseBtn.setOnClickListener(view -> chooseImage());

        saveBtn.setOnClickListener(view -> saveNotes());

    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            imageUri = data.getData();

            addCountryFlagImage.setImageURI(imageUri);
        }
    }
    private void generateNoteEntries() {
        noteEntries.clear();

        String numberStr = durationNo.getText().toString().trim();
        if (TextUtils.isEmpty(numberStr)) {
            return;
        }
        Log.d("DEBUG_DURATION", "Number entered: " + numberStr);

        int number = Integer.parseInt(numberStr);
        Log.d("DEBUG_DURATION", "Parsed number: " + number);
        String type = spinnerDuration.getSelectedItem().toString();

        if (type.equals("Days")) {
            for (int i = 1; i <= number; i++) {
                noteEntries.add(new NoteEntry("Day " + i));
            }
        } else if (type.equals("Weeks")) {
            for (int i = 1; i <= number; i++) {
                noteEntries.add(new NoteEntry("Week " + i));
            }
        } else {
            for (int i = 1; i <= number; i++) {
                noteEntries.add(new NoteEntry("Month " + i));
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void saveNotes() {
        String name = countryName.getText().toString().trim();
        String durationNumberStr = durationNo.getText().toString().trim();
        String durationPeriod = spinnerDuration.getSelectedItem().toString();

        if (name.isEmpty()) {
            countryName.setError("Missing Field");
        }
    }
}