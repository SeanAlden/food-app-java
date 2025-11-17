package com.example.foodapp_java.page.activity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Food;
import com.example.foodapp_java.dataClass.FoodExpDateStock;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FoodDetailActivity extends AppCompatActivity {
    private Food food;
    private ImageView iv;
    private TextView tvName, tvPrice, tvDesc, tvStock;
    private LinearLayout containerExp;
    private Button btnEdit, btnDelete;
    private FirebaseFirestore db;
    private static final String TAG = "FoodDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        food = getIntent().getParcelableExtra("food");
        iv = findViewById(R.id.ivFoodDetailImage);
        tvName = findViewById(R.id.tvFoodDetailName);
        tvPrice = findViewById(R.id.tvFoodDetailPrice);
        tvDesc = findViewById(R.id.tvFoodDetailDescription);
        tvStock = findViewById(R.id.tvFoodDetailStock);
        containerExp = findViewById(R.id.containerExp);
        btnEdit = findViewById(R.id.btnEditFood);
        btnDelete = findViewById(R.id.btnDeleteFood);
        db = FirebaseFirestore.getInstance();

        if (food != null) {
            tvName.setText(food.getName());
            tvPrice.setText(String.format(Locale.getDefault(), "Rp %.0f", food.getPrice()));
            tvDesc.setText(food.getDescription());
            tvStock.setText("Total stock: " + food.getTotalStock());

//            if (food.getImagePath() != null && !food.getImagePath().isEmpty()) {
//                File f = new File(food.getImagePath());
//                if (f.exists()) iv.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
//            }

            if (food.getImagePath() != null && !food.getImagePath().isEmpty()) {
                File f = new File(food.getImagePath());
                if (f.exists()) {
                    iv.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
                } else {
                    // fallback ke default
                    iv.setImageResource(R.drawable.food);
                }
            } else {
                // kalau imagePath kosong
                iv.setImageResource(R.drawable.food);
            }

            loadExpList();
        }

        btnEdit.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, EditFoodActivity.class).putExtra("food", food));
        });

        btnDelete.setOnClickListener(v -> {
            // set status to inactive
            db.collection("foods").document(food.getId()).update("status", "inactive")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Food set to inactive", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void loadExpList() {
        containerExp.removeAllViews();
        db.collection("food_exp_date_stocks")
                .whereEqualTo("foodId", food.getId())
                .orderBy("exp_date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(q -> {
                    for (DocumentSnapshot d : q.getDocuments()) {
                        FoodExpDateStock s = d.toObject(FoodExpDateStock.class);
                        if (s != null) {
                            s.setId(d.getId());
                            // display simple row
                            TextView tv = new TextView(this);
                            Date dt = d.getDate("exp_date");
                            String dateStr = dt == null ? "-" : new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dt);
                            long stock = d.getLong("stock_amount") == null ? 0 : d.getLong("stock_amount");
                            tv.setText("Exp: " + dateStr + " — stock: " + stock);
                            containerExp.addView(tv);
                        }
                    }
                }).addOnFailureListener(e -> Log.w(TAG, "load exp failed", e));
    }
}
