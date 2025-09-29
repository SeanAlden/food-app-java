package com.example.foodapp_java.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.firebaseauth.R;
import com.example.foodapp_java.page.EditCategoryActivity;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Category;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private ArrayList<Category> categoryList;
    private FirebaseFirestore db;

    public CategoryAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);

        holder.tvName.setText(category.getName());
        holder.tvDescription.setText(category.getDescription());
        holder.tvCode.setText("Code: " + category.getCode());

        // Edit
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCategoryActivity.class);
            intent.putExtra("category", category);
            context.startActivity(intent);
        });

        // Delete
        holder.btnDelete.setOnClickListener(v -> {
            db.collection("categories").document(category.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        categoryList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, categoryList.size());
                    });
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvCode;
        Button btnEdit, btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            tvDescription = itemView.findViewById(R.id.tvCategoryDescription);
            tvCode = itemView.findViewById(R.id.tvCategoryCode);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
