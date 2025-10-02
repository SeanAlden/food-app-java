package com.example.foodapp_java.page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Food;
import com.example.foodapp_java.dataClass.FoodExpDateStock;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserFoodDetailActivity extends AppCompatActivity {

    private ImageView ivFood;
    private TextView tvName, tvPrice, tvDesc, tvCategory, tvExp, tvStock, tvQuantity;
    private ImageButton btnMinus, btnPlus;
    private Button btnAddToCart;

    private int currentQty = 1;
    private int maxStock = 0;
    private Date nearestExp;

    private Food food;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_food_detail);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarFoodDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Init
        ivFood = findViewById(R.id.ivFoodDetail);
        tvName = findViewById(R.id.tvFoodNameDetail);
        tvPrice = findViewById(R.id.tvFoodPriceDetail);
        tvDesc = findViewById(R.id.tvFoodDescDetail);
        tvCategory = findViewById(R.id.tvCategoryNameDetail);
        tvExp = findViewById(R.id.tvFoodExpDetail);
        tvStock = findViewById(R.id.tvFoodStockDetail);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        db = FirebaseFirestore.getInstance();

        // Get Food from intent
        food = getIntent().getParcelableExtra("food");
        if (food == null) {
            Toast.makeText(this, "Food not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set data awal
        tvName.setText(food.getName());
        tvPrice.setText("Rp " + food.getPrice());
        tvDesc.setText(food.getDescription());
        if (food.getImagePath() != null && !food.getImagePath().isEmpty()) {
            Glide.with(this).load(food.getImagePath()).placeholder(R.drawable.food).into(ivFood);
        } else {
            ivFood.setImageResource(R.drawable.food);
        }

        // Ambil kategori
        if (food.getCategoryId() != null) {
            db.collection("categories").document(food.getCategoryId())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String catName = doc.getString("name");
                            tvCategory.setText("Category: " + catName);
                        }
                    });
        }

        // Ambil stok + tanggal expired FIFO
        fetchStockFIFO();

        // Toggle qty
        btnMinus.setOnClickListener(v -> {
            if (currentQty > 1) {
                currentQty--;
                tvQuantity.setText(String.valueOf(currentQty));
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (currentQty < maxStock) {
                currentQty++;
                tvQuantity.setText(String.valueOf(currentQty));
            }
        });

        // Add to cart
//        btnAddToCart.setOnClickListener(v -> {
//            Toast.makeText(this, "Added " + currentQty + " item(s) to cart", Toast.LENGTH_SHORT).show();
//        });

        btnAddToCart.setOnClickListener(v -> {
            addToCartAndOpenCart();
        });
    }

    private void fetchStockFIFO() {
        db.collection("food_exp_date_stocks")
                .whereEqualTo("foodId", food.getId())
                .get()
                .addOnSuccessListener(query -> {
                    int total = 0;
                    Date nearest = null;
                    List<FoodExpDateStock> stocks = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : query) {
                        FoodExpDateStock s = doc.toObject(FoodExpDateStock.class);
                        stocks.add(s);
                        total += s.getStock_amount();
                        Date exp = s.getExp_date();
                        if (exp != null && (nearest == null || exp.before(nearest))) {
                            nearest = exp;
                        }
                    }

                    maxStock = total;
                    nearestExp = nearest;

                    tvStock.setText("Stock: " + total);
                    if (nearest != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        tvExp.setText("Exp: " + sdf.format(nearest));
                    } else {
                        tvExp.setText("Exp: -");
                    }
                });
    }

    private void addToCartAndOpenCart() {
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
        if (uid == null) {
            Toast.makeText(this, "Please login to add to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        // check existing cart item for this user+food
        db.collection("carts")
                .whereEqualTo("userId", uid)
                .whereEqualTo("foodId", food.getId())
                .get()
                .addOnSuccessListener(query -> {
                    if (query != null && !query.isEmpty()) {
                        // already exist: update quantity (increment)
                        DocumentSnapshot doc = query.getDocuments().get(0);
                        String cartId = doc.getId();
                        Long curQtyL = doc.getLong("quantity");
                        int curQty = curQtyL == null ? 0 : curQtyL.intValue();
                        int newQty = curQty + currentQty;
                        db.collection("carts").document(cartId)
                                .update("quantity", newQty)
                                .addOnSuccessListener(aVoid -> {
                                    // open cart
                                    Intent i = new Intent(UserFoodDetailActivity.this, CartActivity.class);
                                    startActivity(i);
                                })
                                .addOnFailureListener(e -> Toast.makeText(UserFoodDetailActivity.this, "Failed add to cart", Toast.LENGTH_SHORT).show());
                    } else {
                        // not exist: create new cart doc
                        String id = db.collection("carts").document().getId();
                        java.util.Map<String, Object> data = new java.util.HashMap<>();
                        data.put("id", id);
                        data.put("userId", uid);
                        data.put("foodId", food.getId());
                        data.put("quantity", currentQty);
                        data.put("createdAt", new java.util.Date());

                        db.collection("carts").document(id)
                                .set(data)
                                .addOnSuccessListener(aVoid -> {
                                    Intent i = new Intent(UserFoodDetailActivity.this, CartActivity.class);
                                    startActivity(i);
                                })
                                .addOnFailureListener(e -> Toast.makeText(UserFoodDetailActivity.this, "Failed add to cart", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(UserFoodDetailActivity.this, "Failed add to cart", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
