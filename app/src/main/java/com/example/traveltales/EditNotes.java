package com.example.traveltales;

import android.app.AlertDialog;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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
    Button editCountryFlagChooseBtn, editFromDate, editToDate, editNotesBtn, editLocationBtn, editImagesBtn, saveBtn;
    ImageView editCountryFlagImage;
    String fromDateSelected = "", toDateSelected = "", uploadedFlagUrl = "";
    String noteId;
    String currentImageUrl = "";
    ArrayList<Note> notesList = new ArrayList<>();
    ArrayList<LocationModel> locationList = new ArrayList<>();
    ArrayList<ImageModel> imagesList = new ArrayList<>();
    List<String> uploadedImageUrls = new ArrayList<>();
    Uri imageUri;
    Spinner editSpinnerDuration;
    RecyclerView editNotesRecycler, editLocationRecycler, editImageRecycler;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_notes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ThemeHelper.applyTheme(this);

        backBtn = findViewById(R.id.backBtn);

        editCountryName = findViewById(R.id.editCountryName);
        editDurationNo = findViewById(R.id.editDurationNo);

        editFromDate = findViewById(R.id.editFromDate);
        editToDate = findViewById(R.id.editToDate);
        editNotesBtn = findViewById(R.id.editNotesBtn);
        editLocationBtn = findViewById(R.id.editLocationBtn);
        editImagesBtn = findViewById(R.id.editImagesBtn);
        saveBtn = findViewById(R.id.saveBtn);

        editCountryFlagChooseBtn = findViewById(R.id.editCountryFlagChooseBtn);
        editCountryFlagImage = findViewById(R.id.editCountryFlagImage);

        editSpinnerDuration = findViewById(R.id.editSpinnerDuration);

        editNotesRecycler = findViewById(R.id.editNotesRecycler);
        editLocationRecycler = findViewById(R.id.editLocationRecycler);
        editImageRecycler = findViewById(R.id.editImageRecycler);

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
        imagesList = (ArrayList<ImageModel>) getIntent().getSerializableExtra("images");

        // Spinner
        String[] durationOptions = {"Days", "Weeks", "Months"};
        ArrayAdapter<String> durationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, durationOptions);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        editSpinnerDuration.setAdapter(durationAdapter);

        // Recycler
        NotesAdapter notesAdapter = new NotesAdapter(notesList);
        editNotesRecycler.setLayoutManager(new LinearLayoutManager(this));
        editNotesRecycler.setAdapter(notesAdapter);

        LocationAdapter locationAdapter = new LocationAdapter(locationList);
        editLocationRecycler.setLayoutManager(new LinearLayoutManager(this));
        editLocationRecycler.setAdapter(locationAdapter);

        ImageAdapter imageAdapter = new ImageAdapter(imagesList);
        editImageRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        editImageRecycler.setAdapter(imageAdapter);

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

                notesAdapter.notifyDataSetChanged();
                dialog.dismiss();
            });

            dialog.show();
        });

        editLocationBtn.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

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

        editImagesBtn.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

            intent.setType("image/*");

            // ALLOW MULTIPLE IMAGES
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            imagePickerLauncher.launch(intent);
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
            Uri uri = data.getData();

            MediaManager.get().upload(uri).callback(new UploadCallback() {

                @Override
                public void onStart(String requestId) {}

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {}

                @Override
                public void onSuccess(String requestId, Map resultData) {

                    uploadedFlagUrl = resultData.get("secure_url").toString();

                    editCountryFlagImage.setImageURI(uri);
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {}

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {}

            }).dispatch();
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

        List<Map<String, Object>> locationsData = new ArrayList<>();
        for (LocationModel location : locationList) {

            Map<String, Object> locationMap = new HashMap<>();

            locationMap.put("title", location.getTitle());
            locationMap.put("latitude", location.getLatitude());
            locationMap.put("longitude", location.getLongitude());

            locationsData.add(locationMap);
        }

        // Main map
        Map<String, Object> noteData = new HashMap<>();

        noteData.put("countryName", countryName);

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

        db.collection("users").document(userId).collection("notes").document(noteId).set(noteData).addOnSuccessListener(unused -> {

            Toast.makeText(this, "Notes Updated Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditNotes.this, HomePage.class);
            startActivity(intent);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed To Update Notes", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(EditNotes.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {

                }
            }).dispatch();
        }
    }
    private void setSpinner(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }
}