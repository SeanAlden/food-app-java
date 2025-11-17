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
import com.example.foodapp_java.dataClass.Food;

import java.util.ArrayList;
import java.util.HashSet;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.ViewHolder> {

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Food food);
    }

    public interface OnItemClickListener {
        void onItemClick(Food food);
    }

    private OnFavoriteClickListener favListener;
    private OnItemClickListener itemListener;

    private Context context;
    private ArrayList<Food> list;
    private HashSet<String> favoriteFoodIds;

    public FoodSearchAdapter(Context context, ArrayList<Food> list, HashSet<String> favoriteIds) {
        this.context = context;
        this.list = list;
        this.favoriteFoodIds = favoriteIds;
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.favListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_food_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Food f = list.get(position);

        h.tvName.setText(f.getName());
        h.tvCategory.setText("Category: " + f.getCategoryId());
        h.tvPrice.setText("Rp " + (int) f.getPrice());
        h.tvDesc.setText(f.getDescription());

        Glide.with(context)
                .load(f.getImagePath())
                .placeholder(R.drawable.food)
                .into(h.imgFood);

        boolean isFav = favoriteFoodIds.contains(f.getId());
        h.btnFav.setImageResource(isFav ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);

        h.btnFav.setOnClickListener(v -> {
            if (favListener != null) favListener.onFavoriteClick(f);
        });

        h.itemView.setOnClickListener(v -> {
            if (itemListener != null) itemListener.onItemClick(f);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood, btnFav;
        TextView tvName, tvCategory, tvPrice, tvDesc;

        public ViewHolder(@NonNull View v) {
            super(v);
            imgFood = v.findViewById(R.id.imgFoodItem);
            btnFav = v.findViewById(R.id.btnFavorite);
            tvName = v.findViewById(R.id.tvFoodName);
            tvCategory = v.findViewById(R.id.tvFoodCategory);
            tvPrice = v.findViewById(R.id.tvFoodPrice);
            tvDesc = v.findViewById(R.id.tvFoodDesc);
        }
    }
}
