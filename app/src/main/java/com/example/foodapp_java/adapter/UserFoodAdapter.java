//package com.example.foodapp_java.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.dataClass.Food;
//
//import java.text.SimpleDateFormat;
//import java.util.List;
//import java.util.Locale;
//
//public class UserFoodAdapter extends RecyclerView.Adapter<UserFoodAdapter.ViewHolder> {
//
//    private Context context;
//    private List<Food> list;
//
//    public UserFoodAdapter(Context context, List<Food> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    @NonNull
//    @Override
//    public UserFoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(context).inflate(R.layout.item_food_card, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull UserFoodAdapter.ViewHolder holder, int position) {
//        Food f = list.get(position);
//
//        holder.tvName.setText(f.getName());
//        holder.tvPrice.setText("Rp " + f.getPrice());
//        holder.tvStock.setText("Stock: " + f.getTotalStock());
//        holder.tvExp.setText("Exp: " + (f.getNearestExpDate() != null ? f.getNearestExpDate() : "-"));
//
//        if (f.getNearestExpDate() != null) {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//            holder.tvExp.setText("Exp: " + (sdf.format(f.getNearestExpDate()) != null ? sdf.format(f.getNearestExpDate()) : "-"));
//        } else {
//            holder.tvExp.setText("No expired date");
//        }
//
//        holder.tvCategoryDesc.setText(f.getDescription());
//
//        Glide.with(context).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivImage);
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView ivImage;
//        TextView tvName, tvPrice, tvStock, tvExp, tvCategoryDesc;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ivImage = itemView.findViewById(R.id.ivFoodImage);
//            tvName = itemView.findViewById(R.id.tvFoodName);
//            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
//            tvStock = itemView.findViewById(R.id.tvFoodStock);
//            tvExp = itemView.findViewById(R.id.tvFoodExp);
//            tvCategoryDesc = itemView.findViewById(R.id.tvCategoryDesc);
//        }
//    }
//}

package com.example.foodapp_java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class UserFoodAdapter extends RecyclerView.Adapter<UserFoodAdapter.ViewHolder> {

    private Context context;
    private List<Food> list;
    private FirebaseFirestore db;
    private String uid;

    // Map foodId -> favoriteDocId
    private Map<String, String> favoritesMap = new HashMap<>();

    public UserFoodAdapter(Context context, List<Food> list) {
        this.context = context;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
        this.uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    @NonNull
    @Override
    public UserFoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_food_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFoodAdapter.ViewHolder holder, int position) {
        Food f = list.get(position);

        holder.tvName.setText(f.getName() == null ? "-" : f.getName());
        holder.tvPrice.setText("Rp " + String.valueOf(f.getPrice()));
        holder.tvStock.setText("Stock: " + f.getTotalStock());
        if (f.getNearestExpDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            holder.tvExp.setText("Exp: " + sdf.format(f.getNearestExpDate()));
        } else {
            holder.tvExp.setText("No expired date");
        }

        holder.tvCategoryDesc.setText(f.getDescription() == null ? "-" : f.getDescription());

        // image
        if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
            Glide.with(context).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.food);
        }

        // favorite state
        boolean isFav = (f.getId() != null && favoritesMap.containsKey(f.getId()));
        updateHeartDrawable(holder.btnFavorite, isFav);

        // toggle favorite when clicked
        holder.btnFavorite.setOnClickListener(v -> {
            if (uid == null) {
                Toast.makeText(context, "Please login to favorite", Toast.LENGTH_SHORT).show();
                return;
            }
            if (f.getId() == null) return;

            if (favoritesMap.containsKey(f.getId())) {
                // already favorite -> delete
                String favDocId = favoritesMap.get(f.getId());
                if (favDocId != null) {
                    db.collection("favorites").document(favDocId).delete()
                            .addOnSuccessListener(aVoid -> {
                                favoritesMap.remove(f.getId());
                                notifyItemChanged(position);
                                Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed remove favorite", Toast.LENGTH_SHORT).show());
                }
            } else {
                // not favorite -> add
                String newId = db.collection("favorites").document().getId();
                java.util.Map<String,Object> fav = new java.util.HashMap<>();
                fav.put("id", newId);
                fav.put("userId", uid);
                fav.put("foodId", f.getId());
                fav.put("createdAt", new Date());
                db.collection("favorites").document(newId).set(fav)
                        .addOnSuccessListener(aVoid -> {
                            favoritesMap.put(f.getId(), newId);
                            notifyItemChanged(position);
                            Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed add favorite", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateHeartDrawable(ImageButton btn, boolean isFav) {
        if (isFav) {
            btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
        } else {
            btn.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFavoritesMap(Map<String, String> map) {
        if (map == null) return;
        this.favoritesMap.clear();
        this.favoritesMap.putAll(map);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvStock, tvExp, tvCategoryDesc;
        ImageButton btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivFoodImage);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvStock = itemView.findViewById(R.id.tvFoodStock);
            tvExp = itemView.findViewById(R.id.tvFoodExp);
            tvCategoryDesc = itemView.findViewById(R.id.tvCategoryDesc);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
