package com.example.foodapp_java.page.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.FoodInCategoryAdapter;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategoryDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private ImageButton btnBack;
    private RecyclerView rvFoods;

    private FirebaseFirestore db;
    private FoodInCategoryAdapter adapter;
    private ArrayList<Food> foodList;

    private String categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarFoodDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();

        // get data from intent
        categoryId = getIntent().getStringExtra("categoryId");
        categoryName = getIntent().getStringExtra("categoryName");

        tvTitle = findViewById(R.id.tvTitle);
//        btnBack = findViewById(R.id.btnBack);
        rvFoods = findViewById(R.id.rvFoods);

        // Set AppBar title
        tvTitle.setText(categoryName);

        // Back button
//        btnBack.setOnClickListener(v -> finish());

        // RecyclerView setup
        foodList = new ArrayList<>();
        adapter = new FoodInCategoryAdapter(this, foodList);
//        rvFoods.setLayoutManager(new LinearLayoutManager(this));
        rvFoods.setLayoutManager(new GridLayoutManager(this, 2));
        adapter.setOnItemClickListener(food -> {
            Intent intent = new Intent(this, UserFoodDetailActivity.class);
            intent.putExtra("food", food);
            startActivity(intent);
        });
        rvFoods.setAdapter(adapter);

        loadFoodsByCategory();
    }

    private void loadFoodsByCategory() {
        db.collection("foods")
                .whereEqualTo("category_id", categoryId)
                .addSnapshotListener((@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) -> {
                    if (error != null) return;

                    foodList.clear();

                    if (snapshot != null) {
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Food food = doc.toObject(Food.class);
                            if (food != null) {
                                food.setId(doc.getId());
                                foodList.add(food);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
