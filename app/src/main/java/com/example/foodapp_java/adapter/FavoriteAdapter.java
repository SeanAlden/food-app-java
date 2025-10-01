package com.example.foodapp_java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Favorite;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapter menerima list of Pair(favoriteDocId, Food)
 * Simpler model: we pass list of FavoriteItem objects from fragment.
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.VH> {

    public interface OnRemoveListener {
        void onRemove(int position);
    }

    private Context ctx;
    private List<FavoriteItem> items;
    private OnRemoveListener listener;
    private FirebaseFirestore db;

    public FavoriteAdapter(Context ctx, List<FavoriteItem> items, OnRemoveListener listener) {
        this.ctx = ctx;
        this.items = items;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public FavoriteAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_favorite_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteAdapter.VH holder, int position) {
        FavoriteItem it = items.get(position);
        Food f = it.food;

        holder.tvName.setText(f.getName() == null ? "-" : f.getName());
        holder.tvDesc.setText(f.getDescription() == null ? "-" : f.getDescription());
        holder.tvPrice.setText("Rp " + f.getPrice());

        if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
            Glide.with(ctx).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.iv);
        } else {
            holder.iv.setImageResource(R.drawable.food);
        }

        holder.btnRemove.setOnClickListener(v -> {
            // remove favorite doc
            String favId = it.favorite.getId();
            if (favId != null) {
                db.collection("favorites").document(favId).delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(ctx, "Removed favorite", Toast.LENGTH_SHORT).show();
                            if (listener != null) listener.onRemove(position);
                        })
                        .addOnFailureListener(e -> Toast.makeText(ctx, "Failed remove favorite", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(ctx, "Invalid favorite id", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tvName, tvDesc, tvPrice, btnRemove;
        public VH(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.ivFavFood);
            tvName = itemView.findViewById(R.id.tvFavName);
            tvDesc = itemView.findViewById(R.id.tvFavDesc);
            tvPrice = itemView.findViewById(R.id.tvFavPrice);
            btnRemove = itemView.findViewById(R.id.btnRemoveFav);
        }
    }

    // helper holder class used by fragment
    public static class FavoriteItem {
        public Favorite favorite;
        public Food food;
        public FavoriteItem(Favorite favorite, Food food) {
            this.favorite = favorite;
            this.food = food;
        }
    }
}
