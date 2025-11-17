package com.example.foodapp_java.page.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

//import com.example.firebaseauth.R;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Category;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditCategoryActivity extends AppCompatActivity {
    private EditText etName, etDescription, etCode;
    private Button btnUpdate;
    private FirebaseFirestore db;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        etName = findViewById(R.id.etName);
        etDescription = findViewById(R.id.etDescription);
        etCode = findViewById(R.id.etCode);
        btnUpdate = findViewById(R.id.btnUpdate);

        db = FirebaseFirestore.getInstance();

        // Ambil data dari Intent
        category = getIntent().getParcelableExtra("category");
        if (category != null) {
            etName.setText(category.getName());
            etDescription.setText(category.getDescription());
            etCode.setText(category.getCode());
        }

        btnUpdate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            String code = etCode.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name is required");
                return;
            }

            Map<String, Object> updatedCategory = new HashMap<>();
            updatedCategory.put("id", category.getId());
            updatedCategory.put("name", name);
            updatedCategory.put("description", description);
            updatedCategory.put("code", code);

            db.collection("categories").document(category.getId())
                    .set(updatedCategory)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });
    }
}
