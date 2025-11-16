package com.example.foodapp_java.page;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
//import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.InactiveFoodAdapter;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InactiveFoodActivity extends AppCompatActivity {

    private RecyclerView rvInactiveFoods;
    private InactiveFoodAdapter adapter;
    private List<Food> list = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inactive_food);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        Toolbar toolbar = findViewById(R.id.toolbarInactiveFoods);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Setup Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbarInactiveFoods);
//        toolbar.setTitle("");

//        toolbar.setNavigationOnClickListener(v -> finish());

        rvInactiveFoods = findViewById(R.id.rvInactiveFoods);
        rvInactiveFoods.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InactiveFoodAdapter(this, list);
        rvInactiveFoods.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadInactiveFoods();
    }

    private void loadInactiveFoods() {
        db.collection("foods")
                .whereEqualTo("status", "inactive")
                .get()
                .addOnSuccessListener(q -> {
                    list.clear();
                    for (DocumentSnapshot doc : q.getDocuments()) {
                        Food f = doc.toObject(Food.class);
                        if (f != null) {
                            f.setId(doc.getId());
                            list.add(f);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("InactiveFood", "Failed load inactive foods", e);
                    Toast.makeText(this, "Failed load inactive foods", Toast.LENGTH_SHORT).show();
                });
    }
}
