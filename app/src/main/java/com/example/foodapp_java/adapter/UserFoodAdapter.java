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

import java.util.List;

public class UserFoodAdapter extends RecyclerView.Adapter<UserFoodAdapter.ViewHolder> {

    private Context context;
    private List<Food> list;

    public UserFoodAdapter(Context context, List<Food> list) {
        this.context = context;
        this.list = list;
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

        holder.tvName.setText(f.getName());
        holder.tvPrice.setText("Rp " + f.getPrice());
        holder.tvStock.setText("Stock: " + f.getTotalStock());
        holder.tvExp.setText("Exp: " + (f.getNearestExpDate() != null ? f.getNearestExpDate() : "-"));
        holder.tvCategoryDesc.setText(f.getDescription());

        Glide.with(context).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvStock, tvExp, tvCategoryDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivFoodImage);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvStock = itemView.findViewById(R.id.tvFoodStock);
            tvExp = itemView.findViewById(R.id.tvFoodExp);
            tvCategoryDesc = itemView.findViewById(R.id.tvCategoryDesc);
        }
    }
}
