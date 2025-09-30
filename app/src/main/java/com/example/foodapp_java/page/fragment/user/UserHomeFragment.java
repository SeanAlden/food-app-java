package com.example.foodapp_java.page.fragment.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.UserCategoryAdapter;
import com.example.foodapp_java.adapter.UserFoodAdapter;
import com.example.foodapp_java.dataClass.Category;
import com.example.foodapp_java.dataClass.Food;
import com.example.foodapp_java.dataClass.FoodExpDateStock;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserHomeFragment extends Fragment {

    private TextView tvHeaderUser;
    private ImageView ivProfile;
    private RecyclerView rvFoods, rvCategories;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private UserFoodAdapter UserFoodAdapter;
    private UserCategoryAdapter UserCategoryAdapter;
    private List<Food> foodList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);

        // Init UI
        Toolbar toolbar = view.findViewById(R.id.toolbarUserHome);
        toolbar.setTitle("");

        tvHeaderUser = view.findViewById(R.id.tvHeaderUser);
        ivProfile = view.findViewById(R.id.ivProfile);
        rvFoods = view.findViewById(R.id.rvFoods);
        rvCategories = view.findViewById(R.id.rvCategories);

        rvFoods.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        UserFoodAdapter = new UserFoodAdapter(getContext(), foodList);
        rvFoods.setAdapter(UserFoodAdapter);

        UserCategoryAdapter = new UserCategoryAdapter(getContext(), (ArrayList<Category>) categoryList);
        rvCategories.setAdapter(UserCategoryAdapter);

        // Firebase init
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String name = snapshot.getString("name");
                            tvHeaderUser.setText("Hi, " + (name != null ? name : user.getEmail()));

                            String profileUrl = snapshot.getString("profileUrl");
                            if (profileUrl != null && !profileUrl.isEmpty()) {
                                Glide.with(this).load(profileUrl).circleCrop().into(ivProfile);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                    });
        }

        fetchCategories();
        fetchFoods();

        return view;
    }

    private void fetchCategories() {
        db.collection("categories").get()
                .addOnSuccessListener(query -> {
                    categoryList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Category c = doc.toObject(Category.class);
                        c.setId(doc.getId());
                        categoryList.add(c);
                    }
                    UserCategoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchFoods() {
        db.collection("foods").get()
                .addOnSuccessListener(query -> {
                    foodList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Food f = doc.toObject(Food.class);
                        f.setId(doc.getId());

                        // Join category
//                        db.collection("categories").document(f.getCategoryId())
//                                .get()
//                                .addOnSuccessListener(catDoc -> {
//                                    if (catDoc.exists()) {
//                                        f.setDescription(catDoc.getString("description"));
//                                    }

                                    // Get stock info
                                    db.collection("food_exp_date_stocks")
                                            .whereEqualTo("foodId", f.getId())
                                            .get()
                                            .addOnSuccessListener(stockQuery -> {
                                                int totalStock = 0;
                                                Date nearest = null;

                                                for (QueryDocumentSnapshot sDoc : stockQuery) {
                                                    FoodExpDateStock s = sDoc.toObject(FoodExpDateStock.class);
                                                    totalStock += s.getStock_amount();

//                                                    Date exp = s.getExp_date().toDate();
                                                    Date exp = s.getExp_date();
                                                    if (nearest == null || exp.before(nearest)) {
                                                        nearest = exp;
                                                    }
                                                }

                                                f.setTotalStock(totalStock);
                                                if (nearest != null) {
//                                                    f.setNearestExpDate(new SimpleDateFormat("dd/MM/yyyy").format(nearest));
                                                    f.setNearestExpDate(nearest);
                                                }

                                                foodList.add(f);
                                                UserFoodAdapter.notifyDataSetChanged();
                                            });
//                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load foods", Toast.LENGTH_SHORT).show();
                });
    }
}
