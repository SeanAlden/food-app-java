package com.example.foodapp_java.page.fragment.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.FavoriteAdapter;
import com.example.foodapp_java.dataClass.Favorite;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserFavoriteFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private RecyclerView rvFavorites;
    private FavoriteAdapter adapter;
    private List<FavoriteAdapter.FavoriteItem> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_favorite, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbarUserChat);
        toolbar.setTitle("");

        rvFavorites = view.findViewById(R.id.rvFavorites);
        rvFavorites.setLayoutManager(new GridLayoutManager(getContext(), 2));

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        adapter = new FavoriteAdapter(getContext(), items, position -> {
            // remove from list
            if (position >= 0 && position < items.size()) {
                items.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        rvFavorites.setAdapter(adapter);

        loadFavorites();

        return view;
    }

    private void loadFavorites() {
        if (user == null) return;
        db.collection("favorites")
                .whereEqualTo("userId", user.getUid())
                .get()
                .addOnSuccessListener(q -> {
                    items.clear();
                    if (q.isEmpty()) {
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    for (QueryDocumentSnapshot favDoc : q) {
                        Favorite fav = favDoc.toObject(Favorite.class);
                        fav.setId(favDoc.getId());

                        String foodId = fav.getFoodId();
                        if (foodId == null) continue;

                        db.collection("foods").document(foodId).get()
                                .addOnSuccessListener(foodDoc -> {
                                    if (foodDoc.exists()) {
                                        Food food = foodDoc.toObject(Food.class);
                                        if (food != null) {
                                            food.setId(foodDoc.getId());
                                            items.add(new FavoriteAdapter.FavoriteItem(fav, food));
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // skip this favourite if food not found
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
                });
    }
}
