//package com.example.foodapp_java.adapter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
////import com.example.firebaseauth.R;
//import com.example.foodapp_java.page.activity.EditCategoryActivity;
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.dataClass.Category;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//
//public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
//    private Context context;
//    private ArrayList<Category> categoryList;
//    private FirebaseFirestore db;
//
//    public CategoryAdapter(Context context, ArrayList<Category> categoryList) {
//        this.context = context;
//        this.categoryList = categoryList;
//        this.db = FirebaseFirestore.getInstance();
//    }
//
//    @NonNull
//    @Override
//    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
//        return new CategoryViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
//        Category category = categoryList.get(position);
//
//        holder.tvName.setText(category.getName());
//        holder.tvDescription.setText(category.getDescription());
//        holder.tvCode.setText("Code: " + category.getCode());
//
//        // Edit
//        holder.btnEdit.setOnClickListener(v -> {
//            Intent intent = new Intent(context, EditCategoryActivity.class);
//            intent.putExtra("category", category);
//            context.startActivity(intent);
//        });
//
//        // Delete
//        holder.btnDelete.setOnClickListener(v -> {
//            db.collection("categories").document(category.getId())
//                    .delete()
//                    .addOnSuccessListener(aVoid -> {
//                        categoryList.remove(position);
//                        notifyItemRemoved(position);
//                        notifyItemRangeChanged(position, categoryList.size());
//                    });
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return categoryList.size();
//    }
//
//    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
//        TextView tvName, tvDescription, tvCode;
//        Button btnEdit, btnDelete;
//
//        public CategoryViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvName = itemView.findViewById(R.id.tvCategoryName);
//            tvDescription = itemView.findViewById(R.id.tvCategoryDescription);
//            tvCode = itemView.findViewById(R.id.tvCategoryCode);
//            btnEdit = itemView.findViewById(R.id.btnEdit);
//            btnDelete = itemView.findViewById(R.id.btnDelete);
//        }
//    }
//}

package com.example.foodapp_java.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.page.activity.CategoryDetailActivity;
import com.example.foodapp_java.page.activity.EditCategoryActivity;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Category;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

        // Reset default state of delete button
        holder.btnDelete.setEnabled(true);
        holder.btnDelete.setBackgroundColor(Color.parseColor("#E53935")); // red delete
        holder.btnDelete.setTextColor(Color.WHITE);

        // === PRE-CHECK: apakah kategori dipakai oleh foods? ===
        db.collection("foods")
                .whereEqualTo("category_id", category.getId())
                .get()
                .addOnSuccessListener((QuerySnapshot snapshot) -> {
                    boolean used = snapshot != null && !snapshot.isEmpty();

                    if (used) {
                        // Disable delete button
                        holder.btnDelete.setEnabled(false);
                        holder.btnDelete.setBackgroundColor(Color.parseColor("#BDBDBD")); // grey
                        holder.btnDelete.setTextColor(Color.parseColor("#616161"));
                        holder.btnDelete.setText("Used");
                    } else {
                        // Enable delete button (default state)
                        holder.btnDelete.setEnabled(true);
                        holder.btnDelete.setBackgroundColor(Color.parseColor("#E53935"));
                        holder.btnDelete.setTextColor(Color.WHITE);
                        holder.btnDelete.setText("Delete");
                    }
                })
                .addOnFailureListener(e -> {
                    // Jika gagal check, tetap disable delete untuk keamanan
                    holder.btnDelete.setEnabled(false);
                    holder.btnDelete.setBackgroundColor(Color.parseColor("#BDBDBD"));
                    holder.btnDelete.setText("Error");
                });

//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, CategoryDetailActivity.class);
//            intent.putExtra("category_id", category.getId());
//            intent.putExtra("category_name", category.getName());
//            context.startActivity(intent);
//        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CategoryDetailActivity.class);
            intent.putExtra("categoryId", category.getId());
            intent.putExtra("categoryName", category.getName());
            context.startActivity(intent);
        });

        // === tombol Edit ===
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditCategoryActivity.class);
            intent.putExtra("category", category);
            context.startActivity(intent);
        });

        // === tombol Delete ===
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Kategori")
                    .setMessage("Apakah Anda yakin ingin menghapus kategori \"" + category.getName() + "\"?")
                    .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        db.collection("categories").document(category.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    int pos = holder.getAdapterPosition();
                                    if (pos >= 0 && pos < categoryList.size()) {
                                        categoryList.remove(pos);
                                        notifyItemRemoved(pos);
                                        notifyItemRangeChanged(pos, categoryList.size());
                                    }
                                    Toast.makeText(context, "Category deleted", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .show();
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

