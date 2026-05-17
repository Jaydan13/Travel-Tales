package com.example.traveltales;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import android.app.AlertDialog.Builder;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    ImageButton homeBtn;
    ImageView profilePic;
    Button changeProfilePic, chooseColourBtn, visitListBtn, appInfoBtn, logoutBtn;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ThemeHelper.applyTheme(this);

        homeBtn = findViewById(R.id.homeBtn);
        profilePic = findViewById(R.id.profilePic);
        changeProfilePic = findViewById(R.id.changeProfilePic);
        chooseColourBtn = findViewById(R.id.colourChooseBtn);
        visitListBtn = findViewById(R.id.visitListBtn);
        appInfoBtn = findViewById(R.id.appInfoBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        chooseColourBtn.setOnClickListener(view -> chooseColourDialog());
        appInfoBtn.setOnClickListener(view -> appInfoDialog());

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, HomePage.class);
                startActivity(intent);
            }
        });

        changeProfilePic.setOnClickListener(view -> chooseImage());
        loadProfileImage();

        visitListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, ToVisit.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(Profile.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

    }
    private void chooseColourDialog() {
        Builder builder = new Builder(this);
        View view = getLayoutInflater().inflate(R.layout.colour_choose, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        Spinner colourSpinner = view.findViewById(R.id.colourSpinner);
        String[] colourOptions = {"Blue", "Green", "Orange", "Purple", "Red"};
        ArrayAdapter<String> colourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colourOptions);
        colourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colourSpinner.setAdapter(colourAdapter);

        Button saveColourBtn = view.findViewById(R.id.saveColourBtn);
        saveColourBtn.setOnClickListener(v -> {
            String colour = colourSpinner.getSelectedItem().toString();
            int colourValue;

            switch (colour) {
                case "Green":
                    colourValue = ContextCompat.getColor(this, R.color.green);
                    break;
                case "Orange":
                    colourValue = ContextCompat.getColor(this, R.color.orange);
                    break;
                case "Purple":
                    colourValue = ContextCompat.getColor(this, R.color.purple);
                    break;
                case "Red":
                    colourValue = ContextCompat.getColor(this, R.color.red);
                    break;
                default:
                    colourValue = ContextCompat.getColor(this, R.color.blue);
                    break;
            }
            getSharedPreferences("SettingsPrefs", MODE_PRIVATE)
                    .edit()
                    .putInt("appColourValue", colourValue)
                    .apply();

            ThemeHelper.applyTheme(this);

            dialog.dismiss();
        });
    }
    private void appInfoDialog() {
        Builder builder = new Builder(this);
        View view = getLayoutInflater().inflate(R.layout.app_info, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextView infoText = view.findViewById(R.id.infoText);
        String text = "Travel Tales - Travel Diary\n" +
                "This app allows you to:\n- Add Notes on Countries you have visited\n" +
                "- Create a to-visit list\n- Add Photos and Locations\n- ";
        infoText.setText(text);

        Button closeBtn = view.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
        });
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
            uploadProfileImage();
        }
    }
    private void uploadProfileImage() {

        if (imageUri == null) {
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

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

                Map<String, Object> profileData = new HashMap<>();

                profileData.put("imageUrl", imageUrl);

                db.collection("users").document(userId).collection("profile").document("profileImage").set(profileData).addOnSuccessListener(unused -> {

                    Glide.with(Profile.this).load(imageUrl).into(profilePic);

                    Toast.makeText(Profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {

                Toast.makeText(Profile.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        }).dispatch();
    }
    private void loadProfileImage() {

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("profile").document("profileImage").get().addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.exists()) {

                String imageUrl = documentSnapshot.getString("imageUrl");

                if (imageUrl != null && !imageUrl.isEmpty()) {

                    Glide.with(this).load(imageUrl).into(profilePic);

                } else {
                    profilePic.setImageResource(R.drawable.profile);
                }

            } else {
                profilePic.setImageResource(R.drawable.profile);
            }
        }).addOnFailureListener(e -> {
            profilePic.setImageResource(R.drawable.profile);
        });
    }
}