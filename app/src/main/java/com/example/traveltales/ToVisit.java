package com.example.traveltales;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToVisit extends AppCompatActivity {

    ImageButton backBtn;
    RecyclerView toVisitRecycler;
    FloatingActionButton fabAddVisitBtn;
    List<VisitItem> visitList;
    VisitListAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_to_visit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ThemeHelper.applyTheme(this);

        backBtn = findViewById(R.id.backBtn);
        fabAddVisitBtn = findViewById(R.id.fabAddVisitBtn);

        toVisitRecycler = findViewById(R.id.toVisitRecycler);

        visitList = new ArrayList<>();
        adapter = new VisitListAdapter(visitList);

        toVisitRecycler.setLayoutManager(new LinearLayoutManager(this));
        toVisitRecycler.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ToVisit.this, Profile.class);
                startActivity(intent);
            }
        });

        loadVisitList();

        fabAddVisitBtn.setOnClickListener(v -> {
            Builder builder = new Builder(this);
            View view = getLayoutInflater().inflate(R.layout.add_visit_list, null);
            builder.setView(view);

            AlertDialog dialog = builder.create();
            dialog.show();

            EditText visitCountry = view.findViewById(R.id.visitCountry);
            Button addToListBtn = view.findViewById(R.id.addToListBtn);

            addToListBtn.setOnClickListener(view1 -> {
                String visitCountryName = visitCountry.getText().toString().trim();
                String visitCountryNameLower = visitCountryName.toLowerCase();
                if (visitCountryName.isEmpty()) {
                    visitCountry.setError("Add Country Name!!");
                    return;
                }
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(this, "User Not Logged In", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = mAuth.getCurrentUser().getUid();

                db.collection("users").document(userId).collection("visitList").whereEqualTo("visitCountryNameLower", visitCountryNameLower).get().addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {
                        visitCountry.setError("Country Already Added in List!!");
                        return;
                    }

                    Map<String, Object> toVisitList = new HashMap<>();
                    toVisitList.put("visitCountryName", visitCountryName);
                    toVisitList.put("visitCountryNameLower", visitCountryNameLower);

                    db.collection("users").document(userId).collection("visitList").add(toVisitList).addOnSuccessListener(doc -> {
                        Toast.makeText(this, "Country Added to List", Toast.LENGTH_SHORT).show();
                        loadVisitList();
                        dialog.dismiss();
                    }).addOnFailureListener(e -> Toast.makeText(this, "Error Occurred!!", Toast.LENGTH_SHORT).show());

                });
            });
        });

    }
    protected void onResume() {
        super.onResume();
        loadVisitList();
    }
    private void loadVisitList() {
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(ToVisit.this, Login.class));
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).collection("visitList").get().addOnSuccessListener(queryDocumentSnapshots -> {
            visitList.clear();

            for (DocumentSnapshot doc: queryDocumentSnapshots) {
                String id = doc.getId();
                String visitCountryName = doc.getString("visitCountryName");
                String visitCountryNameLower = doc.getString("visitCountryNameLower");

                visitList.add(new VisitItem(id, visitCountryName, visitCountryNameLower));
            }

            adapter.notifyDataSetChanged();
        });

    }
}