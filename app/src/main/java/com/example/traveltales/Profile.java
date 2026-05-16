package com.example.traveltales;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import android.app.AlertDialog.Builder;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Profile extends AppCompatActivity {

    ImageButton homeBtn;
    ImageView profilePic;
    Button changeProfilePic, chooseColourBtn, visitListBtn, appInfoBtn, logoutBtn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        homeBtn = findViewById(R.id.homeBtn);
        profilePic = findViewById(R.id.profilePic);
        changeProfilePic = findViewById(R.id.changeProfilePic);
        chooseColourBtn = findViewById(R.id.colourChooseBtn);
        visitListBtn = findViewById(R.id.visitListBtn);
        appInfoBtn = findViewById(R.id.appInfoBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        mAuth = FirebaseAuth.getInstance();

        chooseColourBtn.setOnClickListener(view -> chooseColourDialog());
        appInfoBtn.setOnClickListener(view -> appInfoDialog());

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, HomePage.class);
                startActivity(intent);
            }
        });

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
}