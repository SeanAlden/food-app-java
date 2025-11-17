package com.example.foodapp_java.page.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.firebaseauth.R;
import com.example.foodapp_java.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddCategoryActivity extends AppCompatActivity {
    private EditText etName, etDescription, etCode;
    private Button btnSave;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etCode = findViewById(R.id.etCode);
        btnSave = findViewById(R.id.btnSave);

        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String code = etCode.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name is required");
                return;
            }

            // Buat ID otomatis
            String id = db.collection("categories").document().getId();

            Map<String, Object> category = new HashMap<>();
            category.put("id", id);
            category.put("name", name);
            category.put("description", description);
            category.put("code", code);

            db.collection("categories").document(id)
                    .set(category)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}
