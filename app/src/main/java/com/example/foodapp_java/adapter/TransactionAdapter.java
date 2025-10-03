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
import com.example.foodapp_java.dataClass.CartItem;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.VH> {

    private Context ctx;
    private List<CartItem> items;
    private FirebaseFirestore db;

    public TransactionAdapter(Context ctx, List<CartItem> items) {
        this.ctx = ctx;
        this.items = items;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_transaction_detail, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CartItem ci = items.get(position);

        // ambil detail food dari Firestore
        db.collection("foods").document(ci.getFoodId()).get()
                .addOnSuccessListener(doc -> {
                    Food f = doc.toObject(Food.class);
                    if (f == null) return;

                    holder.tvName.setText(f.getName());
                    holder.tvCategory.setText("Category: " + (f.getCategoryId() == null ? "-" : f.getCategoryId()));
                    holder.tvPriceQty.setText("Rp " + (long) f.getPrice() + " x " + ci.getQuantity());

                    if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
                        Glide.with(ctx).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivFood);
                    } else {
                        holder.ivFood.setImageResource(R.drawable.food);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvName, tvCategory, tvPriceQty;

        VH(@NonNull View itemView) {
            super(itemView);
            ivFood = itemView.findViewById(R.id.ivDetailFood);
            tvName = itemView.findViewById(R.id.tvDetailName);
            tvCategory = itemView.findViewById(R.id.tvDetailCategory);
            tvPriceQty = itemView.findViewById(R.id.tvDetailPriceQty);
        }
    }
}
