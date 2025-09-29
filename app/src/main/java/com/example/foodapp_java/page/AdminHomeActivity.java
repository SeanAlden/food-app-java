//package com.example.foodapp_java.page;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
////import com.example.firebaseauth.R;
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.adapter.NewsAdapter;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//
//public class AdminHomeActivity extends AppCompatActivity {
//
//    private TextView tvHeader;
//    private FirebaseAuth auth;
//    private FirebaseUser user;
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_admin_home);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Firebase init
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        user = auth.getCurrentUser();
//
//        // Header
//        tvHeader = findViewById(R.id.tvHeader);
//
//        if (user != null) {
//            db.collection("users").document(user.getUid())
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            String name = documentSnapshot.getString("name");
//                            if (name != null && !name.isEmpty()) {
//                                tvHeader.setText("Hi, " + name);
//                            } else {
//                                tvHeader.setText("Hi, " + user.getEmail());
//                            }
//                        } else {
//                            tvHeader.setText("Hi, " + user.getEmail());
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
//                        tvHeader.setText("Hi, " + user.getEmail());
//                    });
//        }
//
//        // Tombol navigasi
//        Button btnFood = findViewById(R.id.btnFood);
//        Button btnCategory = findViewById(R.id.btnCategory);
//        Button btnCustomer = findViewById(R.id.btnCustomer);
//        Button btnFavorite = findViewById(R.id.btnFavorite);
//
//        // Tombol logout
//        Button btnLogout = findViewById(R.id.btnLogout);
//        btnLogout.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//        });
//
//        // Tampilan status toko
//        TextView tvTotalFoods = findViewById(R.id.tvTotalFoods);
//        TextView tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
//        TextView tvTotalCustomers = findViewById(R.id.tvTotalCustomers);
//
//        // contoh dummy data toko
//        tvTotalFoods.setText("25");
//        tvTotalTransactions.setText("120");
//        tvTotalCustomers.setText("80");
//
//        btnFood.setOnClickListener(v -> startActivity(new Intent(this, FoodActivity.class)));
//        btnCategory.setOnClickListener(v -> startActivity(new Intent(this, CategoryActivity.class)));
//        btnCustomer.setOnClickListener(v -> startActivity(new Intent(this, CustomerActivity.class)));
//        btnFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));
//
//        // RecyclerView setup
//        RecyclerView rvNews = findViewById(R.id.recyclerNews);
//        rvNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//
//        ArrayList<Integer> images = new ArrayList<>();
//        images.add(R.drawable.food); // tambahkan beberapa dummy image
//        images.add(R.drawable.food);
//        images.add(R.drawable.food);
//
//        NewsAdapter adapter = new NewsAdapter(images);
//        rvNews.setAdapter(adapter);
//    }
//}

package com.example.foodapp_java.page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // <-- Import Toolbar
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.NewsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminHomeActivity extends AppCompatActivity {

    private TextView tvHeader;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // HAPUS EdgeToEdge agar tidak menabrak AppBar
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        // HAPUS juga listener WindowInsets
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAdminHome);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // Firebase init
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // Header
        tvHeader = findViewById(R.id.tvHeader);

        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            if (name != null && !name.isEmpty()) {
                                tvHeader.setText("Hi, " + name);
                            } else {
                                tvHeader.setText("Hi, " + user.getEmail());
                            }
                        } else {
                            tvHeader.setText("Hi, " + user.getEmail());
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                        tvHeader.setText("Hi, " + user.getEmail());
                    });
        }

        // Tombol navigasi
        ImageButton btnFood = findViewById(R.id.btnFood);
        ImageButton btnCategory = findViewById(R.id.btnCategory);
        ImageButton btnCustomer = findViewById(R.id.btnCustomer);
        ImageButton btnFavorite = findViewById(R.id.btnFavorite);
        ImageButton btnSupplier = findViewById(R.id.btnSupplier);
        ImageButton btnStockManagement = findViewById(R.id.btnStockManagement);

        // Tombol logout
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Tampilan status toko
        TextView tvTotalFoods = findViewById(R.id.tvTotalFoods);
        TextView tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
        TextView tvTotalCustomers = findViewById(R.id.tvTotalCustomers);

        // contoh dummy data toko
        tvTotalFoods.setText("25");
        tvTotalTransactions.setText("120");
        tvTotalCustomers.setText("80");

        btnFood.setOnClickListener(v -> startActivity(new Intent(this, FoodActivity.class)));
        btnCategory.setOnClickListener(v -> startActivity(new Intent(this, CategoryActivity.class)));
        btnCustomer.setOnClickListener(v -> startActivity(new Intent(this, CustomerActivity.class)));
        btnFavorite.setOnClickListener(v -> startActivity(new Intent(this, FavoriteActivity.class)));
        btnStockManagement.setOnClickListener(v -> startActivity(new Intent(this, StockManagementActivity.class)));
        btnSupplier.setOnClickListener(v -> startActivity(new Intent(this, SupplierActivity.class)));

        // RecyclerView setup
        RecyclerView rvNews = findViewById(R.id.recyclerNews);
        rvNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ArrayList<Integer> images = new ArrayList<>();
        images.add(R.drawable.food); // tambahkan beberapa dummy image
        images.add(R.drawable.food);
        images.add(R.drawable.food);

        NewsAdapter adapter = new NewsAdapter(images);
        rvNews.setAdapter(adapter);
    }
}