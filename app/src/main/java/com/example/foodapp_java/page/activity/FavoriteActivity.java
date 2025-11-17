//package com.example.foodapp_java.page;
//
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
////import com.example.firebaseauth.R;
//import com.example.foodapp_java.R;
//
//public class FavoriteActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_favorite);
////        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
////            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
////            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
////            return insets;
////        });
//
//        // Setup Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbarFavorite);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("");
//        }
//    }
//}

package com.example.foodapp_java.page.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.UserFavoriteAdapter;
import com.example.foodapp_java.dataClass.Favorite;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FavoriteActivity extends AppCompatActivity {

    private RecyclerView rvFavorites;
    private UserFavoriteAdapter adapter;
    private List<Favorite> favorites = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

//        // Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbarFavorite);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle("");
//        }
        Toolbar toolbar = findViewById(R.id.toolbarFavorite);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvFavorites = findViewById(R.id.rvFavorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserFavoriteAdapter(this, favorites, favDocId -> {
            // optional callback when removed
            // snapshot listener will refresh the list — no extra action needed
        });
        rvFavorites.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        listenFavoritesRealtime();
    }

    private void listenFavoritesRealtime() {
        db.collection("favorites")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snap, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("FavoriteActivity", "Listen failed", e);
                            Toast.makeText(FavoriteActivity.this, "Failed load favorites", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (snap == null) return;

                        favorites.clear();
                        for (DocumentSnapshot d : snap.getDocuments()) {
                            Favorite f = d.toObject(Favorite.class);
                            if (f != null) {
                                // ensure id field present (adapter uses it to delete)
                                if (f.getId() == null || f.getId().isEmpty()) {
                                    f.setId(d.getId());
                                }
                                favorites.add(f);
                            }
                        }
                        adapter.setList(favorites);
                    }
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
