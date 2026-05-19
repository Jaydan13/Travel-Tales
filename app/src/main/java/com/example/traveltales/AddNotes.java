package com.example.traveltales;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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
    Button addCountryFlagChooseBtn, saveBtn, fromDate, toDate, addNotesBtn, addLocationBtn, addImagesBtn;
    ImageView addCountryFlagImage;
    String fromDateSelected = "", toDateSelected = "", uploadedFlagUrl = "";
    ImageButton backBtn;
    EditText countryName, durationNo;
    Spinner spinnerDuration;
    ArrayList<Note> notesList = new ArrayList<>();
    ArrayList<LocationModel> locationList = new ArrayList<>();
    ArrayList<ImageModel> imagesList = new ArrayList<>();
    List<String> uploadedImageUrls = new ArrayList<>();
    NotesAdapter notesAdapter;
    LocationAdapter locationAdapter;
    ImageAdapter imageAdapter;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    RecyclerView addNotesRecycler, addLocationRecycler, addImageRecycler;
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
        addLocationBtn = findViewById(R.id.addLocationBtn);
        addImagesBtn = findViewById(R.id.addImagesBtn);

        addCountryFlagImage = findViewById(R.id.addCountryFlagImage);

        countryName = findViewById(R.id.countryName);
        durationNo = findViewById(R.id.durationNo);

        spinnerDuration = findViewById(R.id.spinnerDuration);

        addNotesRecycler = findViewById(R.id.addNotesRecycler);
        addLocationRecycler = findViewById(R.id.addLocationRecycler);
        addImageRecycler = findViewById(R.id.addImageRecycler);

        notesAdapter = new NotesAdapter(notesList);
        addNotesRecycler.setLayoutManager(new LinearLayoutManager(this));
        addNotesRecycler.setAdapter(notesAdapter);

        locationAdapter = new LocationAdapter(locationList);
        addLocationRecycler.setLayoutManager(new LinearLayoutManager(this));
        addLocationRecycler.setAdapter(locationAdapter);

        imageAdapter = new ImageAdapter(imagesList);
        addImageRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        addImageRecycler.setAdapter(imageAdapter);

        String countryNameVisit = getIntent().getStringExtra("countryName");
        countryName.setText(countryNameVisit);

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
            Button saveNotesDialogBtn = dialog.findViewById(R.id.saveNotesDialogBtn);

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

            saveNotesDialogBtn.setOnClickListener(v -> {

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
                notesAdapter.notifyDataSetChanged();
                dialog.dismiss();
            });
            dialog.show();
        });

        addLocationBtn.setOnClickListener(v -> {

            Builder builder = new Builder(AddNotes.this);

            View dialogLocationView = getLayoutInflater().inflate(R.layout.item_location_entry, null);
            builder.setView(dialogLocationView);

            AlertDialog dialog = builder.create();
            dialog.show();

            // Dialog Views
            RecyclerView locationRecycler = dialogLocationView.findViewById(R.id.locationRecycler);
            Button dialogAddLocationBtn = dialogLocationView.findViewById(R.id.dialogAddLocationBtn);
            Button dialogSaveLocationBtn = dialogLocationView.findViewById(R.id.dialogSaveLocationBtn);

            List<LocationModel> tempLocationList = new ArrayList<>();
            LocationAdapter tempAdapter = new LocationAdapter(tempLocationList);
            locationRecycler.setLayoutManager(new LinearLayoutManager(this));
            locationRecycler.setAdapter(tempAdapter);

            dialogAddLocationBtn.setOnClickListener(view -> {

                MapPickerDialog mapPickerDialog = new MapPickerDialog();

                mapPickerDialog.setOnLocationSelectedListener(location -> {
                    tempLocationList.add(location);
                    tempAdapter.notifyDataSetChanged();
                });

                mapPickerDialog.show(getSupportFragmentManager(), "map_picker");

            });

            dialogSaveLocationBtn.setOnClickListener(view -> {

                locationList.clear();

                locationList.addAll(tempLocationList);
                locationAdapter.notifyDataSetChanged();

                dialog.dismiss();
            });

        });

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                Intent data = result.getData();

                // MULTIPLE IMAGES SELECTED
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    for (int i = 0; i < count; i++) {

                        Uri imageUri = data.getClipData().getItemAt(i).getUri();

                        imagesList.add(new ImageModel(imageUri.toString()));
                    }
                }

                // SINGLE IMAGE SELECTED
                else if (data.getData() != null) {
                    Uri imageUri = data.getData();

                    imagesList.add(new ImageModel(imageUri.toString()));
                }

                imageAdapter.notifyDataSetChanged();
            }
        });

        addImagesBtn.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

            intent.setType("image/*");

            // ALLOW MULTIPLE IMAGES
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            imagePickerLauncher.launch(intent);
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
            Uri uri = data.getData();

            MediaManager.get().upload(uri).callback(new UploadCallback() {

                @Override
                public void onStart(String requestId) {}

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {}

                @Override
                public void onSuccess(String requestId, Map resultData) {

                    uploadedFlagUrl = resultData.get("secure_url").toString();

                    addCountryFlagImage.setImageURI(uri);
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {}

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {}

            }).dispatch();
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

        List<Map<String, Object>> locationsData = new ArrayList<>();
        for (LocationModel location : locationList) {

            Map<String, Object> locationMap = new HashMap<>();

            locationMap.put("title", location.getTitle());
            locationMap.put("latitude", location.getLatitude());
            locationMap.put("longitude", location.getLongitude());

            locationsData.add(locationMap);
        }

        // Main data map
        Map<String, Object> noteData = new HashMap<>();

        noteData.put("countryName", countryName);
        noteData.put("imageUrl", uploadedFlagUrl);
        noteData.put("durationNumber", durationNumberStr);
        noteData.put("durationPeriod", durationPeriod);
        noteData.put("fromDate", fromDateSelected);
        noteData.put("toDate", toDateSelected);
        noteData.put("notes", notesData);
        noteData.put("locations", locationsData);
        noteData.put("images", uploadedImageUrls);

        uploadImagesAndSave(userId, noteData);
    }
    private void saveToFirestore(String userId, Map<String, Object> noteData) {

        db.collection("users").document(userId).collection("notes").add(noteData).addOnSuccessListener(documentReference -> {

            Toast.makeText(this, "Notes Saved Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed To Save Notes", Toast.LENGTH_SHORT).show();
        });
    }
    private void uploadImagesAndSave(String userId, Map<String, Object> noteData) {
        uploadedImageUrls.clear();

        if (imagesList.isEmpty()) {
            noteData.put("images", new ArrayList<>());

            saveToFirestore(userId, noteData);
            return;
        }

        final int totalImages = imagesList.size();
        final int[] uploadedCount = {0};

        for (ImageModel image : imagesList) {
            Uri uri = Uri.parse(image.getImageUri());

            MediaManager.get().upload(uri).callback(new UploadCallback() {

                @Override
                public void onStart(String requestId) {

                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {

                }

                @Override
                public void onSuccess(String requestId, Map resultData) {

                    String url = resultData.get("secure_url").toString();

                    uploadedImageUrls.add(url);

                    uploadedCount[0]++;

                    if (uploadedCount[0] == totalImages) {
                        noteData.put("images", uploadedImageUrls);

                        saveToFirestore(userId, noteData);
                    }
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {

                    Toast.makeText(AddNotes.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {}
            }).dispatch();
        }
    }
}