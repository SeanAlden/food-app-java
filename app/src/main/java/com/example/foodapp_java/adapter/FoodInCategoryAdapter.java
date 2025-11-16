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

//public class FoodInCategoryAdapter extends RecyclerView.Adapter<FoodInCategoryAdapter.ViewHolder> {
//
//    private Context context;
//    private ArrayList<Food> foodList;
//
//    public FoodInCategoryAdapter(Context context, ArrayList<Food> foodList) {
//        this.context = context;
//        this.foodList = foodList;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(context).inflate(R.layout.item_food_in_category, parent, false);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Food food = foodList.get(position);
//
//        holder.tvName.setText(food.getName());
//        holder.tvPrice.setText("Rp " + food.getPrice());
//        holder.tvDesc.setText(food.getDescription());
//        holder.tvStatus.setText(food.getStatus());
//
//        if (food.getStatus().equalsIgnoreCase("available")) {
//            holder.tvStatus.setTextColor(0xFF2E7D32);
//        } else {
//            holder.tvStatus.setTextColor(0xFFC62828);
//        }
//
//        Glide.with(context)
//                .load(food.getImagePath())
//                .placeholder(R.drawable.food)
//                .into(holder.imgFood);
//    }
//
//    @Override
//    public int getItemCount() {
//        return foodList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        ImageView imgFood;
//        TextView tvName, tvPrice, tvDesc, tvStatus;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            imgFood = itemView.findViewById(R.id.imgFood);
//            tvName = itemView.findViewById(R.id.tvName);
//            tvPrice = itemView.findViewById(R.id.tvPrice);
//            tvDesc = itemView.findViewById(R.id.tvDesc);
//            tvStatus = itemView.findViewById(R.id.tvStatus);
//        }
//    }
//}

public class FoodInCategoryAdapter extends RecyclerView.Adapter<FoodInCategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Food> foodList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Food food);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FoodInCategoryAdapter(Context context, ArrayList<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_food_in_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.tvName.setText(food.getName());
        holder.tvPrice.setText("Rp " + food.getPrice());
        holder.tvDesc.setText(food.getDescription());
        holder.tvStatus.setText(food.getStatus());

        if (food.getStatus().equalsIgnoreCase("available")) {
            holder.tvStatus.setTextColor(0xFF2E7D32);
        } else {
            holder.tvStatus.setTextColor(0xFFC62828);
        }

        Glide.with(context)
                .load(food.getImagePath())
                .placeholder(R.drawable.food)
                .into(holder.imgFood);

        // ---- CLICK ITEM ----
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(food);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView tvName, tvPrice, tvDesc, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgFood = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}

