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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditNotes extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    ImageButton backBtn;
    EditText editCountryName, editDurationNo;
    Button editCountryFlagChooseBtn, editFromDate, editToDate, editNotesBtn, saveBtn;
    ImageView editCountryFlagImage;
    String fromDateSelected = "", toDateSelected = "";
    String noteId;
    String currentImageUrl = "";
    ArrayList<Note> notesList = new ArrayList<>();
    ArrayList<LocationModel> locationList = new ArrayList<>();
    Uri imageUri;
    Spinner editSpinnerDuration;
    RecyclerView editNotesRecycler;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    NotesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_notes);

        ThemeHelper.applyTheme(this);

        backBtn = findViewById(R.id.backBtn);

        editCountryName = findViewById(R.id.editCountryName);
        editDurationNo = findViewById(R.id.editDurationNo);

        editFromDate = findViewById(R.id.editFromDate);
        editToDate = findViewById(R.id.editToDate);
        editNotesBtn = findViewById(R.id.editNotesBtn);
        saveBtn = findViewById(R.id.saveBtn);

        editCountryFlagChooseBtn = findViewById(R.id.editCountryFlagChooseBtn);
        editCountryFlagImage = findViewById(R.id.editCountryFlagImage);

        editSpinnerDuration = findViewById(R.id.editSpinnerDuration);

        editNotesRecycler = findViewById(R.id.editNotesRecycler);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Intent data
        noteId = getIntent().getStringExtra("id");

        String countryName = getIntent().getStringExtra("countryName");
        currentImageUrl = getIntent().getStringExtra("imageUrl");
        String durationNumber = getIntent().getStringExtra("durationNumber");
        String durationPeriod = getIntent().getStringExtra("durationPeriod");
        String fromDate = getIntent().getStringExtra("fromDate");
        String toDate = getIntent().getStringExtra("toDate");

        notesList = (ArrayList<Note>) getIntent().getSerializableExtra("notes");
        locationList = (ArrayList<LocationModel>) getIntent().getSerializableExtra("locations");

        // Spinner
        String[] durationOptions = {"Days", "Weeks", "Months"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        editSpinnerDuration.setAdapter(durationAdapter);

        // Recycler
        adapter = new NotesAdapter(notesList);
        editNotesRecycler.setLayoutManager(new LinearLayoutManager(this));
        editNotesRecycler.setAdapter(adapter);

        // Set existing data
        editCountryName.setText(countryName);
        editDurationNo.setText(durationNumber);
        setSpinner(editSpinnerDuration, durationPeriod);
        editFromDate.setText(fromDate);
        editToDate.setText(toDate);
        fromDateSelected = fromDate;
        toDateSelected = toDate;
        if (currentImageUrl == null || currentImageUrl.isEmpty()) {
            editCountryFlagImage.setImageResource(R.drawable.photo);
        } else {
            Glide.with(this).load(currentImageUrl).into(editCountryFlagImage);
        }

        // Back button
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EditNotes.this, HomePage.class);
            startActivity(intent);
        });

        // Choose image
        editCountryFlagChooseBtn.setOnClickListener(v -> chooseImage());

        // From date
        editFromDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

                month++;

                fromDateSelected = dayOfMonth + "/" + month + "/" + year;

                editFromDate.setText(fromDateSelected);

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        // To date
        editToDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

                month++;

                toDateSelected = dayOfMonth + "/" + month + "/" + year;

                editToDate.setText(toDateSelected);

                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.show();
        });

        editNotesBtn.setOnClickListener(view -> {

            String duration = editDurationNo.getText().toString().trim();
            String durationType = editSpinnerDuration.getSelectedItem().toString();

            if (duration.isEmpty()) {
                editDurationNo.setError("Enter Duration!!!");
                return;
            }

            int durationInt = Integer.parseInt(duration);

            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_notes);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            LinearLayout notesContainer = dialog.findViewById(R.id.notesContainer);
            Button saveDialogBtn = dialog.findViewById(R.id.saveNotesDialogBtn);

            ArrayList<EditText> editTexts = new ArrayList<>();
            LayoutInflater inflater = LayoutInflater.from(this);

            notesContainer.removeAllViews();

            for (int i = 0; i < durationInt; i++) {

                View noteView = inflater.inflate(R.layout.item_note_entry, notesContainer, false);

                TextView dayTitle = noteView.findViewById(R.id.dayTitle);
                EditText noteEditText = noteView.findViewById(R.id.noteEditText);

                String title;

                if (durationType.equals("Days")) {
                    title = "Day " + (i + 1);
                } else if (durationType.equals("Weeks")) {
                    title = (durationInt == 1) ? "Day " + (i + 1) : "Week " + (i + 1);
                } else {
                    title = (durationInt == 1) ? "Week " + (i + 1) : "Month " + (i + 1);
                }

                dayTitle.setText(title);

                if (i < notesList.size()) {
                    noteEditText.setText(notesList.get(i).getNote());
                }

                notesContainer.addView(noteView);
                editTexts.add(noteEditText);
            }

            saveDialogBtn.setOnClickListener(v -> {

                notesList.clear();

                for (int i = 0; i < editTexts.size(); i++) {

                    String noteText = editTexts.get(i).getText().toString();

                    String title;

                    if (durationType.equals("Days")) {
                        title = "Day " + (i + 1);
                    } else if (durationType.equals("Weeks")) {
                        title = (durationInt == 1) ? "Day " + (i + 1) : "Week " + (i + 1);
                    } else {
                        title = (durationInt == 1) ? "Week " + (i + 1) : "Month " + (i + 1);
                    }

                    notesList.add(new Note(title, noteText));
                }

                adapter.notifyDataSetChanged();
                dialog.dismiss();
            });

            dialog.show();
        });

        // Save
        saveBtn.setOnClickListener(v -> updateNotes());
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
            editCountryFlagImage.setImageURI(imageUri);
        }
    }
    private void updateNotes() {
        String countryName = editCountryName.getText().toString().trim();
        String durationNumberStr = editDurationNo.getText().toString().trim();
        String durationPeriod = editSpinnerDuration.getSelectedItem().toString();

        // Validation
        if (countryName.isEmpty()) {
            editCountryName.setError("Missing Field");
            return;
        }

        if (durationNumberStr.isEmpty()) {
            editDurationNo.setError("Missing Field");
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

        // Convert notes
        List<Map<String, String>> notesData = new ArrayList<>();

        for (Note note : notesList) {
            Map<String, String> noteMap = new HashMap<>();

            noteMap.put("title", note.getTitle());
            noteMap.put("note", note.getNote());

            notesData.add(noteMap);
        }

        // Main map
        Map<String, Object> noteData = new HashMap<>();

        noteData.put("countryName", countryName);

        noteData.put("durationNumber", durationNumberStr);
        noteData.put("durationPeriod", durationPeriod);
        noteData.put("fromDate", fromDateSelected);
        noteData.put("toDate", toDateSelected);
        noteData.put("notes", notesData);

        // Upload new image if selected
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

                    String uploadedImageUrl = resultData.get("secure_url").toString();

                    noteData.put("imageUrl", uploadedImageUrl);

                    saveToFirestore(userId, noteData);
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Toast.makeText(EditNotes.this, "Image Upload Failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {

                }
            }).dispatch();

        } else {
            // Keep old image
            noteData.put("imageUrl", currentImageUrl);

            saveToFirestore(userId, noteData);
        }
    }

    private void saveToFirestore(String userId, Map<String, Object> noteData) {

        db.collection("users").document(userId).collection("notes").document(noteId).set(noteData).addOnSuccessListener(unused -> {

            Toast.makeText(this, "Notes Updated Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditNotes.this, HomePage.class);
            startActivity(intent);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed To Update Notes", Toast.LENGTH_SHORT).show();
        });
    }

    private void setSpinner(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }
}