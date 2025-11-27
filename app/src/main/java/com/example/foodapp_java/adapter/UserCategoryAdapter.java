package com.example.foodapp_java.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Category;
import com.example.foodapp_java.page.activity.CategoryDetailActivity;
import com.example.foodapp_java.page.activity.UserCategoryDetailActivity;

import java.util.List;

public class UserCategoryAdapter extends RecyclerView.Adapter<UserCategoryAdapter.ViewHolder> {

    private Context context;
    private List<Category> list;

    public UserCategoryAdapter(Context context, List<Category> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCategoryAdapter.ViewHolder holder, int position) {
        Category c = list.get(position);

        holder.tvName.setText(c.getName());
        holder.ivImage.setImageResource(R.drawable.category); // default image

        // === CLICK LISTENER UNTUK PINDAH HALAMAN ===
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, UserCategoryDetailActivity.class);
            i.putExtra("categoryId", c.getId());
            i.putExtra("categoryName", c.getName());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCategoryImage);
            tvName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
