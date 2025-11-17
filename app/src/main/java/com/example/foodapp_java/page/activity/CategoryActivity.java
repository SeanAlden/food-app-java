//package com.example.firebaseauth;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.firebaseauth.adapter.CategoryAdapter;
//import com.example.firebaseauth.dataClass.Category;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//
//public class CategoryActivity extends AppCompatActivity {
//    private RecyclerView recyclerCategories;
//    private CategoryAdapter adapter;
//    private ArrayList<Category> categoryList;
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_category);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        recyclerCategories = findViewById(R.id.recyclerCategories);
//        recyclerCategories.setLayoutManager(new LinearLayoutManager(this));
//
//        categoryList = new ArrayList<>();
//        adapter = new CategoryAdapter(this, categoryList);
//        recyclerCategories.setAdapter(adapter);
//
//        db = FirebaseFirestore.getInstance();
//
//        // Load data dari Firestore
//        db.collection("categories").get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    categoryList.clear();
//                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                        Category category = new Category(
//                                doc.getId(),
//                                doc.getString("name"),
//                                doc.getString("description"),
//                                doc.getString("code")
//                        );
//                        categoryList.add(category);
//                    }
//                    adapter.notifyDataSetChanged();
//                });
//
//        // Tombol tambah kategori
//        FloatingActionButton fabAddCategory = findViewById(R.id.fabAddCategory);
//        fabAddCategory.setOnClickListener(v -> {
//            startActivity(new Intent(this, AddCategoryActivity.class));
//        });
//    }
//}

package com.example.foodapp_java.page.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.firebaseauth.R;
import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.CategoryAdapter;
import com.example.foodapp_java.dataClass.Category;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ArrayList<Category> categoryList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarCategory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Realtime listener
        db.collection("categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Category category = dc.getDocument().toObject(Category.class);

                        switch (dc.getType()) {
                            case ADDED:
                                categoryList.add(category);
                                break;
                            case MODIFIED:
                                for (int i = 0; i < categoryList.size(); i++) {
                                    if (categoryList.get(i).getId().equals(category.getId())) {
                                        categoryList.set(i, category);
                                        break;
                                    }
                                }
                                break;
                            case REMOVED:
                                categoryList.removeIf(c -> c.getId().equals(category.getId()));
                                break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        // Tombol tambah kategori
        FloatingActionButton fab = findViewById(R.id.fabAddCategory);
        fab.setOnClickListener(v -> startActivity(new Intent(this, AddCategoryActivity.class)));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
