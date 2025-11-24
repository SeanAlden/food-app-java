package com.example.foodapp_java.page.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.FoodSearchAdapter;
import com.example.foodapp_java.dataClass.Favorite;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class FoodSearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private RecyclerView rv;
    private FoodSearchAdapter adapter;

    private FirebaseFirestore db;
    private String uid;

    private ArrayList<Food> fullList = new ArrayList<>();
    private HashSet<String> favoriteIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_search);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarCategoryDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getUid();

        etSearch = findViewById(R.id.etSearchFood);
        rv = findViewById(R.id.rvFoodSearch);

        rv.setLayoutManager(new LinearLayoutManager(this));

        loadFavorites();
        loadFoodData();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFood(s.toString());
            }
        });
    }

    private void loadFavorites() {
        db.collection("favorites")
                .whereEqualTo("userId", uid)
                .addSnapshotListener((snap, e) -> {
                    if (snap == null) return;
                    favoriteIds.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot d : snap.getDocuments()) {
                        favoriteIds.add(d.getString("foodId"));
                    }
                    if (adapter != null) adapter.notifyDataSetChanged();
                });
    }

    private void loadFoodData() {
        db.collection("foods").get()
                .addOnSuccessListener(snap -> {
                    fullList.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot d : snap.getDocuments()) {
                        Food f = d.toObject(Food.class);
                        if (f == null) continue;
                        f.setId(d.getId());
                        fullList.add(f);
                    }
                    setupRecycler();
                });
    }

    private void setupRecycler() {
        adapter = new FoodSearchAdapter(this, fullList, favoriteIds);

        adapter.setOnFavoriteClickListener(food -> toggleFavorite(food));

        adapter.setOnItemClickListener(food -> {
            Intent i = new Intent(this, FoodDetailActivity.class);
            i.putExtra("food", food);
            startActivity(i);
        });

        rv.setAdapter(adapter);
    }

    private void toggleFavorite(Food food) {
        if (favoriteIds.contains(food.getId())) {
            // remove favorite
            db.collection("favorites")
                    .whereEqualTo("userId", uid)
                    .whereEqualTo("foodId", food.getId())
                    .get()
                    .addOnSuccessListener(snap -> {
                        for (com.google.firebase.firestore.DocumentSnapshot d : snap.getDocuments()) {
                            db.collection("favorites").document(d.getId()).delete();
                        }
                    });
        } else {
            // add favorite
            Favorite fav = new Favorite(null, uid, food.getId(), new Date());
            db.collection("favorites")
                    .add(fav)
                    .addOnSuccessListener(ref -> ref.update("id", ref.getId()));
        }
    }

    private void filterFood(String keyword) {
        keyword = keyword.toLowerCase();

        ArrayList<Food> filtered = new ArrayList<>();
        for (Food f : fullList) {
            if (f.getName().toLowerCase().contains(keyword) ||
                    f.getCategoryId().toLowerCase().contains(keyword) ||
                    f.getDescription().toLowerCase().contains(keyword))
            {
                filtered.add(f);
            }
        }

        adapter = new FoodSearchAdapter(this, filtered, favoriteIds);
        adapter.setOnFavoriteClickListener(this::toggleFavorite);

        adapter.setOnItemClickListener(food -> {
            Intent i = new Intent(this, FoodDetailActivity.class);
            i.putExtra("food", food);
            startActivity(i);
        });

        rv.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
