//package com.example.foodapp_java.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.dataClass.Favorite;
//import com.example.foodapp_java.dataClass.Food;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class UserFavoriteAdapter extends RecyclerView.Adapter<UserFavoriteAdapter.VH> {
//
//    private Context ctx;
//    private List<Favorite> list;
//    private FirebaseFirestore db;
//
//    // cache agar tidak query berulang
//    private Map<String, Food> foodCache = new HashMap<>();
//    private Map<String, String> userNameCache = new HashMap<>();
//
//    public interface OnFavoriteChanged {
//        void onRemoved(String favDocId);
//    }
//
//    private OnFavoriteChanged callback;
//
//    public UserFavoriteAdapter(Context ctx, List<Favorite> list, OnFavoriteChanged cb) {
//        this.ctx = ctx;
//        this.list = list;
//        this.db = FirebaseFirestore.getInstance();
//        this.callback = cb;
//    }
//
//    @NonNull
//    @Override
//    public UserFavoriteAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(ctx).inflate(R.layout.item_user_favorite_card, parent, false);
//        return new VH(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull UserFavoriteAdapter.VH holder, int position) {
//        Favorite fav = list.get(position);
//
//        holder.tvFavFoodName.setText("Loading...");
//        holder.tvFavFoodPrice.setText("Rp 0");
//        holder.tvFavCategory.setText("Category: -");
//        holder.tvFavUserName.setText("User: -");
//        holder.ivFavFood.setImageResource(R.drawable.food);
//
//        if (fav == null) return;
//
//        String foodId = fav.getFoodId();
//        String userId = fav.getUserId();
//
//        // 1) Load food (cache first)
//        if (foodId != null) {
//            if (foodCache.containsKey(foodId)) {
//                Food f = foodCache.get(foodId);
//                bindFoodToHolder(f, holder);
//            } else {
//                db.collection("foods").document(foodId).get()
//                        .addOnSuccessListener(doc -> {
//                            if (doc != null && doc.exists()) {
//                                Food f = doc.toObject(Food.class);
//                                if (f != null) {
//                                    foodCache.put(foodId, f);
//                                    bindFoodToHolder(f, holder);
//                                } else {
//                                    holder.tvFavFoodName.setText("Unknown");
//                                }
//                            } else {
//                                holder.tvFavFoodName.setText("Unknown");
//                            }
//                        })
//                        .addOnFailureListener(e -> {
//                            holder.tvFavFoodName.setText("Unknown");
//                        });
//            }
//        } else {
//            holder.tvFavFoodName.setText("Unknown");
//        }
//
//        // 2) Load user name (cache)
//        if (userId != null) {
//            if (userNameCache.containsKey(userId)) {
//                holder.tvFavUserName.setText("User: " + userNameCache.get(userId));
//            } else {
//                db.collection("users").document(userId).get()
//                        .addOnSuccessListener(doc -> {
//                            if (doc != null && doc.exists()) {
//                                String name = doc.getString("name");
//                                if (name == null || name.isEmpty()) name = doc.getString("email");
//                                if (name == null) name = "User";
//                                userNameCache.put(userId, name);
//                                holder.tvFavUserName.setText("User: " + name);
//                            } else {
//                                holder.tvFavUserName.setText("User: -");
//                            }
//                        })
//                        .addOnFailureListener(e -> holder.tvFavUserName.setText("User: -"));
//            }
//        } else {
//            holder.tvFavUserName.setText("User: -");
//        }
//
//        // remove favorite
//        holder.btnRemoveFavorite.setOnClickListener(v -> {
//            if (fav.getId() == null) {
//                Toast.makeText(ctx, "Invalid favorite id", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            // delete doc from firestore
//            db.collection("favorites").document(fav.getId()).delete()
//                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(ctx, "Removed favorite", Toast.LENGTH_SHORT).show();
//                        if (callback != null) callback.onRemoved(fav.getId());
//                    })
//                    .addOnFailureListener(e -> Toast.makeText(ctx, "Failed to remove", Toast.LENGTH_SHORT).show());
//        });
//    }
//
//    private void bindFoodToHolder(Food f, VH holder) {
//        if (f == null) return;
//        holder.tvFavFoodName.setText(f.getName() == null ? "-" : f.getName());
//        holder.tvFavFoodPrice.setText("Rp " + (long) f.getPrice());
//        holder.tvFavCategory.setText("Category: " + (f.getCategoryId() == null ? "-" : f.getCategoryId()));
//        if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
//            Glide.with(ctx).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivFavFood);
//        } else {
//            holder.ivFavFood.setImageResource(R.drawable.food);
//        }
//
//        // optionally try to fetch category name (if categoryId present) — light approach: show id for now.
//        // (If you want category name, we can add one firestore call and cache category names similar to UserFavoriteAdapter)
//    }
//
//    @Override
//    public int getItemCount() {
//        return list == null ? 0 : list.size();
//    }
//
//    public void setList(List<Favorite> newList) {
//        this.list = newList;
//        notifyDataSetChanged();
//    }
//
//    static class VH extends RecyclerView.ViewHolder {
//        ImageView ivFavFood;
//        TextView tvFavFoodName, tvFavFoodPrice, tvFavCategory, tvFavUserName;
//        ImageButton btnRemoveFavorite;
//
//        public VH(@NonNull View itemView) {
//            super(itemView);
//            ivFavFood = itemView.findViewById(R.id.ivFavFood);
//            tvFavFoodName = itemView.findViewById(R.id.tvFavFoodName);
//            tvFavFoodPrice = itemView.findViewById(R.id.tvFavFoodPrice);
//            tvFavCategory = itemView.findViewById(R.id.tvFavCategory);
//            tvFavUserName = itemView.findViewById(R.id.tvFavUserName);
//            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);
//        }
//    }
//}

//package com.example.foodapp_java.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.dataClass.Favorite;
//import com.example.foodapp_java.dataClass.Food;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class UserFavoriteAdapter extends RecyclerView.Adapter<UserFavoriteAdapter.VH> {
//
//    private Context ctx;
//    private List<Favorite> list;
//    private FirebaseFirestore db;
//
//    // Cache biar hemat query
//    private Map<String, Food> foodCache = new HashMap<>();
//    private Map<String, String> userNameCache = new HashMap<>();
//    private Map<String, String> categoryNameCache = new HashMap<>();
//
//    public interface OnFavoriteChanged {
//        void onRemoved(String favDocId);
//    }
//
//    private OnFavoriteChanged callback;
//
//    public UserFavoriteAdapter(Context ctx, List<Favorite> list, OnFavoriteChanged cb) {
//        this.ctx = ctx;
//        this.list = list;
//        this.db = FirebaseFirestore.getInstance();
//        this.callback = cb;
//    }
//
//    @NonNull
//    @Override
//    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(ctx).inflate(R.layout.item_user_favorite_card, parent, false);
//        return new VH(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull VH holder, int position) {
//        Favorite fav = list.get(position);
//        if (fav == null) return;
//
//        holder.tvFavFoodName.setText("Loading...");
//        holder.tvFavFoodPrice.setText("Rp 0");
//        holder.tvFavCategory.setText("Category: -");
//        holder.tvFavUserName.setText("User: -");
//        holder.ivFavFood.setImageResource(R.drawable.food);
//
//        String foodId = fav.getFoodId();
//        String userId = fav.getUserId();
//
//        // 1️⃣ Load FOOD (dari cache / Firestore)
//        if (foodId != null) {
//            if (foodCache.containsKey(foodId)) {
//                bindFoodToHolder(foodCache.get(foodId), holder);
//            } else {
//                db.collection("foods").document(foodId).get()
//                        .addOnSuccessListener(doc -> {
//                            if (doc.exists()) {
//                                Food f = doc.toObject(Food.class);
//                                if (f != null) {
//                                    foodCache.put(foodId, f);
//                                    bindFoodToHolder(f, holder);
//                                }
//                            } else {
//                                holder.tvFavFoodName.setText("Unknown food");
//                            }
//                        })
//                        .addOnFailureListener(e -> holder.tvFavFoodName.setText("Unknown food"));
//            }
//        }
//
//        // 2️⃣ Load USER NAME
//        if (userId != null) {
//            if (userNameCache.containsKey(userId)) {
//                holder.tvFavUserName.setText("User: " + userNameCache.get(userId));
//            } else {
//                db.collection("users").document(userId).get()
//                        .addOnSuccessListener(doc -> {
//                            if (doc.exists()) {
//                                String name = doc.getString("name");
//                                if (name == null || name.isEmpty()) name = doc.getString("email");
//                                if (name == null) name = "User";
//                                userNameCache.put(userId, name);
//                                holder.tvFavUserName.setText("User: " + name);
//                            }
//                        })
//                        .addOnFailureListener(e -> holder.tvFavUserName.setText("User: -"));
//            }
//        }
//
//        // 3️⃣ Tombol remove favorite
////        holder.btnRemoveFavorite.setOnClickListener(v -> {
////            if (fav.getId() == null) {
////                Toast.makeText(ctx, "Invalid favorite id", Toast.LENGTH_SHORT).show();
////                return;
////            }
////            db.collection("favorites").document(fav.getId()).delete()
////                    .addOnSuccessListener(aVoid -> {
////                        Toast.makeText(ctx, "Removed favorite", Toast.LENGTH_SHORT).show();
////                        if (callback != null) callback.onRemoved(fav.getId());
////                    })
////                    .addOnFailureListener(e -> Toast.makeText(ctx, "Failed to remove", Toast.LENGTH_SHORT).show());
////        });
//    }
//
//    /**
//     * Mengikat data makanan ke tampilan holder
//     */
//    private void bindFoodToHolder(Food f, VH holder) {
//        if (f == null) return;
//
//        holder.tvFavFoodName.setText(f.getName() != null ? f.getName() : "-");
//        holder.tvFavFoodPrice.setText("Rp " + (long) f.getPrice());
//
//        // Load image
//        if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
//            Glide.with(ctx).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivFavFood);
//        } else {
//            holder.ivFavFood.setImageResource(R.drawable.food);
//        }
//
//        // Ambil nama kategori dari Firestore (dengan cache)
//        String categoryId = f.getCategoryId();
//        if (categoryId == null || categoryId.isEmpty()) {
//            holder.tvFavCategory.setText("Category: -");
//            return;
//        }
//
//        if (categoryNameCache.containsKey(categoryId)) {
//            holder.tvFavCategory.setText("Category: " + categoryNameCache.get(categoryId));
//        } else {
//            db.collection("categories").document(categoryId).get()
//                    .addOnSuccessListener(catDoc -> {
//                        if (catDoc.exists()) {
//                            String catName = catDoc.getString("name");
//                            if (catName == null) catName = "-";
//                            categoryNameCache.put(categoryId, catName);
//                            holder.tvFavCategory.setText("Category: " + catName);
//                        } else {
//                            holder.tvFavCategory.setText("Category: -");
//                        }
//                    })
//                    .addOnFailureListener(e -> holder.tvFavCategory.setText("Category: -"));
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return list == null ? 0 : list.size();
//    }
//
//    public void setList(List<Favorite> newList) {
//        this.list = newList;
//        notifyDataSetChanged();
//    }
//
//    static class VH extends RecyclerView.ViewHolder {
//        ImageView ivFavFood;
//        TextView tvFavFoodName, tvFavFoodPrice, tvFavCategory, tvFavUserName;
////        ImageButton btnRemoveFavorite;
//
//        public VH(@NonNull View itemView) {
//            super(itemView);
//            ivFavFood = itemView.findViewById(R.id.ivFavFood);
//            tvFavFoodName = itemView.findViewById(R.id.tvFavFoodName);
//            tvFavFoodPrice = itemView.findViewById(R.id.tvFavFoodPrice);
//            tvFavCategory = itemView.findViewById(R.id.tvFavCategory);
//            tvFavUserName = itemView.findViewById(R.id.tvFavUserName);
////            btnRemoveFavorite = itemView.findViewById(R.id.btnRemoveFavorite);
//        }
//    }
//}

package com.example.foodapp_java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Favorite;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFavoriteAdapter extends RecyclerView.Adapter<UserFavoriteAdapter.VH> {

    private final Context ctx;
    private List<Favorite> list;
    private final FirebaseFirestore db;

    private final Map<String, Food> foodCache = new HashMap<>();
    private final Map<String, String> categoryNameCache = new HashMap<>();
    private final Map<String, Integer> foodFavCountCache = new HashMap<>();


    public interface OnFavoriteChanged {
        void onRemoved(String favDocId);
    }

    public interface OnFavoriteClick {
        void onClick(Food food);
    }

    private final OnFavoriteChanged callback;

    public UserFavoriteAdapter(Context ctx, List<Favorite> list, OnFavoriteChanged cb) {
        this.ctx = ctx;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
        this.callback = cb;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_user_favorite_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Favorite fav = list.get(position);
        if (fav == null) return;

        holder.tvFavFoodName.setText("Loading...");
        holder.tvFavFoodPrice.setText("Rp 0");
        holder.tvFavCategory.setText("Category: -");
        holder.tvFavUserCount.setText("Favorited by 0 users");
        holder.ivFavFood.setImageResource(R.drawable.food);

        String foodId = fav.getFoodId();
        if (foodId == null || foodId.isEmpty()) return;

        // 1️⃣ Ambil data FOOD
        if (foodCache.containsKey(foodId)) {
            bindFoodToHolder(foodCache.get(foodId), holder);
        } else {
            db.collection("foods").document(foodId).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            Food f = doc.toObject(Food.class);
                            if (f != null) {
                                foodCache.put(foodId, f);
                                bindFoodToHolder(f, holder);
                            }
                        } else {
                            holder.tvFavFoodName.setText("Unknown food");
                        }
                    })
                    .addOnFailureListener(e -> holder.tvFavFoodName.setText("Unknown food"));
        }

        // 2️⃣ Hitung jumlah user yang memfavoritkan makanan ini
        if (foodFavCountCache.containsKey(foodId)) {
            holder.tvFavUserCount.setText("Favorited by " + foodFavCountCache.get(foodId) + " users");
        } else {
            db.collection("favorites")
                    .whereEqualTo("foodId", foodId)
                    .get()
                    .addOnSuccessListener(qs -> {
                        int count = qs.size();
                        foodFavCountCache.put(foodId, count);
                        holder.tvFavUserCount.setText("Favorited by " + count + " users");
                    })
                    .addOnFailureListener(e -> holder.tvFavUserCount.setText("Favorited by 0 users"));
        }
    }

    private void bindFoodToHolder(Food f, VH holder) {
        if (f == null) return;

        holder.tvFavFoodName.setText(f.getName() != null ? f.getName() : "-");
        holder.tvFavFoodPrice.setText("Rp " + (long) f.getPrice());

        // Gambar makanan
        if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
            Glide.with(ctx).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivFavFood);
        } else {
            holder.ivFavFood.setImageResource(R.drawable.food);
        }

        // Nama kategori (cache)
        String categoryId = f.getCategoryId();
        if (categoryId == null || categoryId.isEmpty()) {
            holder.tvFavCategory.setText("Category: -");
            return;
        }

        if (categoryNameCache.containsKey(categoryId)) {
            holder.tvFavCategory.setText("Category: " + categoryNameCache.get(categoryId));
        } else {
            db.collection("categories").document(categoryId).get()
                    .addOnSuccessListener(catDoc -> {
                        if (catDoc.exists()) {
                            String catName = catDoc.getString("name");
                            if (catName == null) catName = "-";
                            categoryNameCache.put(categoryId, catName);
                            holder.tvFavCategory.setText("Category: " + catName);
                        } else {
                            holder.tvFavCategory.setText("Category: -");
                        }
                    })
                    .addOnFailureListener(e -> holder.tvFavCategory.setText("Category: -"));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setList(List<Favorite> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivFavFood;
        TextView tvFavFoodName, tvFavFoodPrice, tvFavCategory, tvFavUserCount;

        public VH(@NonNull View itemView) {
            super(itemView);
            ivFavFood = itemView.findViewById(R.id.ivFavFood);
            tvFavFoodName = itemView.findViewById(R.id.tvFavFoodName);
            tvFavFoodPrice = itemView.findViewById(R.id.tvFavFoodPrice);
            tvFavCategory = itemView.findViewById(R.id.tvFavCategory);
            tvFavUserCount = itemView.findViewById(R.id.tvFavUserName); // pakai id lama
        }
    }
}

