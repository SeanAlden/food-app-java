package com.example.foodapp_java.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Food;
import com.example.foodapp_java.page.activity.EditFoodActivity;
import com.example.foodapp_java.page.activity.FoodDetailActivity;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.VH> {
    private Context ctx;
    private List<Food> list;
    private Map<String, String> categoryMap = new HashMap<>();

    public FoodAdapter(Context ctx, List<Food> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_food, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Food f = list.get(position);
        String categoryName = "-";
        if (f.getCategoryId() != null && categoryMap.containsKey(f.getCategoryId())) {
            categoryName = categoryMap.get(f.getCategoryId());
        }
        holder.tvName.setText(f.getName());
//        holder.tvCategory.setText(f.getCategoryId() != null ? f.getCategoryId() : "-");
//        holder.tvCategory.setText("Category: " + getCategoryName(f.getCategoryId()));
        holder.tvCategory.setText("Category: " + categoryName);
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Rp %.0f", f.getPrice()));
        holder.tvDesc.setText(f.getDescription() == null ? "" : f.getDescription());

        int totalStock = f.getTotalStock();
        if (f.getNearestExpDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            holder.tvStockExp.setText("Nearest exp: " + sdf.format(f.getNearestExpDate()) + " (stock: " + totalStock + ")");
        } else {
            holder.tvStockExp.setText("Nearest exp: - (stock: " + totalStock + ")");
        }

        if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
            File img = new File(f.getImagePath());
            if (img.exists()) {
                holder.img.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
            } else {
                holder.img.setImageResource(R.drawable.food);
            }
        } else {
            holder.img.setImageResource(R.drawable.food);
        }

        holder.btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(ctx, EditFoodActivity.class);
            i.putExtra("food", f);
            ctx.startActivity(i);
        });

        holder.btnDelete.setOnClickListener(v -> {
            com.google.firebase.firestore.FirebaseFirestore.getInstance().collection("foods").document(f.getId()).update("status", "inactive");
        });

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, FoodDetailActivity.class);
            i.putExtra("food", f);
            ctx.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setCategoryMap(Map<String, String> categoryMap) {
        this.categoryMap = categoryMap;
    }

    private String getCategoryName(String categoryId) {
        if (categoryId == null) return "-";
        switch (categoryId) {
            case "beverage": return "Beverage";
            case "snack": return "Snack";
            case "main_course": return "Main Course";
            default: return categoryId; // fallback tampilkan ID kalau belum di-mapping
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvCategory, tvPrice, tvStockExp, tvDesc;
        Button btnEdit, btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgFood);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStockExp = itemView.findViewById(R.id.tvStockExp);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
