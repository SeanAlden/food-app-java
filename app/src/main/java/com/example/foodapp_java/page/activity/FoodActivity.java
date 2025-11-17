package com.example.foodapp_java.page.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.FoodAdapter;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodActivity extends AppCompatActivity {

    private RecyclerView rvFoods;
    private FoodAdapter adapter;
    private List<Food> foods;
    private FirebaseFirestore db;
    private static final String TAG = "FoodActivity";

    private ListenerRegistration foodListener;

    private Map<String, String> categoryMap = new HashMap<>();

    @Override
    protected void onStart() {
        super.onStart();
        listenFoods();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (foodListener != null) {
            foodListener.remove();
            foodListener = null;
        }
    }

    private void listenFoods() {
        foodListener = db.collection("foods")
                .whereEqualTo("status", "active")
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "listen error", e);
                        Toast.makeText(this, "Listen failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (querySnapshot == null) return;

                    foods.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Food f = doc.toObject(Food.class);
                        if (f != null) {
                            f.setId(doc.getId());
                            foods.add(f);
                            loadExpStocksForFood(f, foods.size() - 1);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food);
        Toolbar toolbar = findViewById(R.id.toolbarFood);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvFoods = findViewById(R.id.rvFoods);
        rvFoods.setLayoutManager(new LinearLayoutManager(this));
        foods = new ArrayList<>();
        adapter = new FoodAdapter(this, foods);
        rvFoods.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        findViewById(R.id.fabAddFood).setOnClickListener(v -> {
            startActivity(new Intent(this, AddFoodActivity.class));
        });

        loadFoods();
        loadCategories();
    }

//    private void loadFoods() {
//        // query active foods (you can remove whereEqualTo to show all)
//        db.collection("foods").whereEqualTo("status", "active")
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    foods.clear();
//                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
//                        Food f = doc.toObject(Food.class);
//                        if (f != null) {
//                            f.setId(doc.getId());
//                            // populate helper defaults
//                            f.setTotalStock(0);
//                            f.setNearestExpDate(null);
//                            foods.add(f);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                    // for each food, fetch its stock/nearest exp
//                    for (Food food : foods) {
//                        loadExpStocksForFood(food);
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "failed load foods", e);
//                    Toast.makeText(this, "Failed load foods", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void loadExpStocksForFood(Food food) {
//        // query exp stocks for this food, order by exp_date asc
//        db.collection("food_exp_date_stocks")
//                .whereEqualTo("foodId", food.getId())
//                .orderBy("exp_date", Query.Direction.ASCENDING)
//                .get()
//                .addOnSuccessListener(q -> {
//                    int total = 0;
//                    Date nearest = null;
//                    for (DocumentSnapshot d : q.getDocuments()) {
//                        Long stock = d.getLong("stock_amount");
//                        java.util.Date expDate = d.getDate("exp_date");
//                        if (stock != null) total += stock.intValue();
//
//                        // find nearest upcoming exp date with stock > 0
//                        if (expDate != null) {
//                            if (nearest == null) {
//                                nearest = expDate;
//                            } else if (expDate.before(nearest)) {
//                                nearest = expDate;
//                            }
//                        }
//                    }
//                    food.setTotalStock(total);
//                    food.setNearestExpDate(nearest);
//                    runOnUiThread(() -> adapter.notifyDataSetChanged());
//                })
//                .addOnFailureListener(e -> Log.w(TAG, "failed load exp stocks", e));
//    }

    private void loadFoods() {
        db.collection("foods").whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    foods.clear(); // Hapus data lama
                    if (querySnapshot.isEmpty()) {
                        adapter.notifyDataSetChanged(); // Jika tidak ada makanan, update UI
                        return;
                    }

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Food f = doc.toObject(Food.class);
                        if (f != null) {
                            f.setId(doc.getId());
                            // JANGAN set stok ke 0 di sini lagi
                            foods.add(f);

                            // Langsung panggil loader stok untuk item yang baru ditambahkan
                            // dan berikan posisinya di adapter
                            loadExpStocksForFood(f, foods.size() - 1);
                        }
                    }
                    // Hapus notifyDataSetChanged() dari sini untuk mencegah flash
                    // adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "failed load foods", e);
                    Toast.makeText(this, "Failed to load foods", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    categoryMap.clear();
                    for (DocumentSnapshot doc : querySnapshot) {
                        String id = doc.getString("id");
                        String name = doc.getString("name");
                        if (id != null && name != null) {
                            categoryMap.put(id, name);
                        }
                    }
                    adapter.setCategoryMap(categoryMap); // update ke adapter
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed load categories", e);
                });
    }

    // Modifikasi fungsi ini untuk menerima posisi item
    private void loadExpStocksForFood(final Food food, final int position) {
        db.collection("food_exp_date_stocks")
                .whereEqualTo("foodId", food.getId())
                .orderBy("exp_date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(q -> {
                    int total = 0;
                    Date nearest = null;

                    // Logika untuk mencari tanggal terdekat yang stoknya > 0
                    Date now = new Date();
                    for (DocumentSnapshot d : q.getDocuments()) {
                        Long stock = d.getLong("stock_amount");
                        Date expDate = d.getDate("exp_date");

                        if (stock != null) {
                            total += stock.intValue();
                        }

                        // Cari tanggal kadaluarsa terdekat yang akan datang dan stoknya masih ada
                        if (expDate != null && expDate.after(now) && stock != null && stock > 0) {
                            if (nearest == null || expDate.before(nearest)) {
                                nearest = expDate;
                            }
                        }
                    }

                    food.setTotalStock(total);
                    food.setNearestExpDate(nearest);

                    // Update HANYA item yang spesifik, ini jauh lebih efisien
                    runOnUiThread(() -> adapter.notifyItemChanged(position));
                })
                .addOnFailureListener(e -> {
                    // Tambahkan Toast di sini agar Anda tahu jika query gagal
                    Log.w(TAG, "Failed to load stock for food: " + food.getName(), e);
                    Toast.makeText(this, "Failed to load stock for " + food.getName(), Toast.LENGTH_SHORT).show();

                    // Set default value on failure
                    food.setTotalStock(0);
                    food.setNearestExpDate(null);
                    runOnUiThread(() -> adapter.notifyItemChanged(position));
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
