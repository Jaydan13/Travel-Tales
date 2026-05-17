package com.example.traveltales;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
    Button addCountryFlagChooseBtn, saveBtn, fromDate, toDate, addNotesBtn;
    ImageView addCountryFlagImage;
    String fromDateSelected = "", toDateSelected = "";
    ImageButton backBtn;
    EditText countryName, durationNo;
    Spinner spinnerDuration;
    ArrayList<Note> notesList = new ArrayList<>();
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

        ThemeHelper.applyTheme(this);

        backBtn = findViewById(R.id.backBtn);
        saveBtn = findViewById(R.id.saveBtn);
        fromDate = findViewById(R.id.fromDate);
        toDate = findViewById(R.id.toDate);
        addCountryFlagChooseBtn = findViewById(R.id.addCountryFlagChooseBtn);
        addNotesBtn = findViewById(R.id.addNotesBtn);

        addCountryFlagImage = findViewById(R.id.addCountryFlagImage);

        countryName = findViewById(R.id.countryName);
        durationNo = findViewById(R.id.durationNo);

        spinnerDuration = findViewById(R.id.spinnerDuration);

        addNotesRecycler = findViewById(R.id.addNotesRecycler);

        adapter = new NotesAdapter(notesList);

        addNotesRecycler.setLayoutManager(new LinearLayoutManager(this));
        addNotesRecycler.setAdapter(adapter);

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

        addCountryFlagChooseBtn.setOnClickListener(view -> chooseImage());

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

        addNotesBtn.setOnClickListener(view -> {

            String duration = durationNo.getText().toString().trim();
            String durationType = spinnerDuration.getSelectedItem().toString();

            if (duration.isEmpty()) {
                durationNo.setError("Enter Duration!!!");
                return;
            }

            int durationInt = Integer.parseInt(duration);

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_notes);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            LinearLayout notesContainer = dialog.findViewById(R.id.notesContainer);
            Button saveDialogBtn = dialog.findViewById(R.id.saveDialogBtn);

            ArrayList<EditText> editTexts = new ArrayList<>();
            LayoutInflater inflater = LayoutInflater.from(this);

            notesContainer.removeAllViews();

            int count = 0;
            String labelType = "Day";

            if (durationType.equals("Days")) {
                count = durationInt;
                labelType = "Day";
            } else if (durationType.equals("Weeks")) {
                if (durationInt == 1) {
                    count = 7;
                    labelType = "Day";
                } else {
                    count = durationInt;
                    labelType = "Week";
                }
            } else if (durationType.equals("Months")) {
                if (durationInt == 1) {
                    count = 4;
                    labelType = "Week";
                } else {
                    count = durationInt;
                    labelType = "Month";
                }
            }

            for (int i = 1; i <= count; i++) {

                View noteView = inflater.inflate(R.layout.item_note_entry, notesContainer, false);

                TextView dayTitle = noteView.findViewById(R.id.dayTitle);
                EditText noteEditText = noteView.findViewById(R.id.noteEditText);

                dayTitle.setText(labelType + " " + i + ":");

                notesContainer.addView(noteView);
                editTexts.add(noteEditText);
            }

            saveDialogBtn.setOnClickListener(v -> {

                notesList.clear();

                for (int i = 0; i < editTexts.size(); i++) {

                    String noteText = editTexts.get(i).getText().toString();
                    String title = "";

                    if (durationType.equals("Days")) {
                        title = "Day " + (i + 1);

                    } else if (durationType.equals("Weeks")) {
                        if (duration.equals("1")) {
                            title = "Day " + (i + 1);
                        } else {
                            title = "Week " + (i + 1);
                        }
                    } else if (durationType.equals("Months")) {
                        if (duration.equals("1")) {
                            title = "Week " + (i + 1);
                        } else {
                            title = "Month " + (i + 1);
                        }
                    }

                    notesList.add(new Note(title, noteText));
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            });
            dialog.show();
        });

        saveBtn.setOnClickListener(view -> saveNotes());

    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addCountryFlagImage.setImageURI(imageUri);
        }
    }
    private void saveNotes() {

        String countryName = this.countryName.getText().toString().trim();
        String durationNumberStr = durationNo.getText().toString().trim();
        String durationPeriod = spinnerDuration.getSelectedItem().toString();

        // Validation
        if (countryName.isEmpty()) {
            this.countryName.setError("Missing Field");
            return;
        }

        if (durationNumberStr.isEmpty()) {
            durationNo.setError("Missing Field");
            return;
        }

        if (fromDateSelected.isEmpty()) {
            Toast.makeText(this, "Select From Date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (toDateSelected.isEmpty()) {
            Toast.makeText(this, "Select To Date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (notesList.isEmpty()) {
            Toast.makeText(this, "Add Notes First", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // Convert notes list into Firestore format
        List<Map<String, String>> notesData = new ArrayList<>();

        for (Note note : notesList) {

            Map<String, String> noteMap = new HashMap<>();

            noteMap.put("title", note.getTitle());
            noteMap.put("note", note.getNote());

            notesData.add(noteMap);
        }

        // Main data map
        Map<String, Object> noteData = new HashMap<>();

        noteData.put("countryName", countryName);
        noteData.put("durationNumber", durationNumberStr);
        noteData.put("durationPeriod", durationPeriod);
        noteData.put("fromDate", fromDateSelected);
        noteData.put("toDate", toDateSelected);
        noteData.put("notes", notesData);

        // If image uploaded
        if (imageUri != null) {

            MediaManager.get().upload(imageUri).callback(new UploadCallback() {

                @Override
                public void onStart(String requestId) {

                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {

                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    String imageUrl = resultData.get("secure_url").toString();
                    noteData.put("imageUrl", imageUrl);
                    saveToFirestore(userId, noteData);
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Toast.makeText(AddNotes.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {

                }
            }).dispatch();

        } else {
            noteData.put("imageUrl", "");
            saveToFirestore(userId, noteData);
        }
    }

    private void saveToFirestore(String userId, Map<String, Object> noteData) {

        db.collection("users").document(userId).collection("notes").add(noteData).addOnSuccessListener(documentReference -> {

            Toast.makeText(this, "Notes Saved Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed To Save Notes", Toast.LENGTH_SHORT).show();
        });
    }
}