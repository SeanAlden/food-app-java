package com.example.foodapp_java.page.activity;

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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InactiveFoodActivity extends AppCompatActivity {

    private RecyclerView rvInactiveFoods;
    private InactiveFoodAdapter adapter;
    private List<Food> list = new ArrayList<>();
    private FirebaseFirestore db;

    private static final String TAG = "InactiveFoodActivity";

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

        Toolbar toolbar = findViewById(R.id.toolbarInactiveFoods);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvInactiveFoods = findViewById(R.id.rvInactiveFoods);
        rvInactiveFoods.setLayoutManager(new LinearLayoutManager(this));

        adapter = new InactiveFoodAdapter(this, list);
        rvInactiveFoods.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadCategories(() -> loadInactiveFoods());
//        loadInactiveFoods();
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
                            loadExpStocksForFood(f, list.size() - 1);
                        }
                    }
//                    calculateStockAndExp();
//                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("InactiveFood", "Failed load inactive foods", e);
                    Toast.makeText(this, "Failed load inactive foods", Toast.LENGTH_SHORT).show();
                });
    }

//    private void calculateStockAndExp() {
//        db.collection("entry_stocks").get().addOnSuccessListener(entries -> {
//            db.collection("outgoing_stocks").get().addOnSuccessListener(outs -> {
//
//                for (Food f : list) {
//                    int stock = 0;
//                    Date nearest = null;
//
//                    // Hitung dari EntryStock
//                    for (DocumentSnapshot e : entries) {
//                        if (f.getId().equals(e.getString("foodId"))) {
//                            int qty = e.getLong("quantity").intValue();
//                            stock += qty;
//
//                            Date exp = e.getDate("expDate");
//                            if (exp != null) {
//                                if (nearest == null || exp.before(nearest))
//                                    nearest = exp;
//                            }
//                        }
//                    }
//
//                    // Kurangi dari OutgoingStock
//                    for (DocumentSnapshot o : outs) {
//                        if (f.getId().equals(o.getString("foodId"))) {
//                            int qty = o.getLong("quantity").intValue();
//                            stock -= qty;
//                        }
//                    }
//
//                    f.setTotalStock(stock);
//                    f.setNearestExpDate(nearest);
//                }
//
//                adapter.notifyDataSetChanged();
//            });
//        });
//    }

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

    private void loadCategories(Runnable onDone) {
        db.collection("categories").get()
                .addOnSuccessListener(q -> {
                    for (DocumentSnapshot d : q.getDocuments()) {
                        String id = d.getId();
                        String name = d.getString("name");
                        if (id != null && name != null) {
                            adapter.addCategory(id, name);
                        }
                    }
                    onDone.run();
                })
                .addOnFailureListener(e -> onDone.run());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
