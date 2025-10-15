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

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataBaseOffline.TransactionOffline;
import com.example.foodapp_java.dataClass.CartItem;
import com.example.foodapp_java.dataClass.Food;
import com.example.foodapp_java.dataClass.FoodExpDateStock;
import com.example.foodapp_java.page.TransactionDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminTransactionListAdapter extends RecyclerView.Adapter<AdminTransactionListAdapter.VH> {
    private final Context ctx;
    private List<TransactionOffline> list;
    private final FirebaseFirestore db;

    // 🔹 Cache email agar hemat query
    private final Map<String, String> userEmailCache = new HashMap<>();

    public AdminTransactionListAdapter(Context ctx, List<TransactionOffline> list) {
        this.ctx = ctx;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setList(List<TransactionOffline> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminTransactionListAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_admin_transaction, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminTransactionListAdapter.VH holder, int position) {
        TransactionOffline trx = list.get(position);

        holder.tvTransTotal.setText("Total: Rp " + (long) trx.getTotalPrice());
        holder.tvTransExp.setText("Exp date: -");
        holder.tvTransName.setText("Transaction #" + trx.getId());
        holder.tvUserEmail.setText("Loading email...");

        // 1️⃣ Ambil email user dari Firestore pakai userId
        String userId = trx.getUserId();
        if (userId != null && !userId.isEmpty()) {
            if (userEmailCache.containsKey(userId)) {
                holder.tvUserEmail.setText(userEmailCache.get(userId));
            } else {
                db.collection("users").document(userId).get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                String email = doc.getString("email");
                                if (email == null || email.isEmpty()) email = "Unknown";
                                userEmailCache.put(userId, email);
                                holder.tvUserEmail.setText(email);
                            } else {
                                holder.tvUserEmail.setText("Unknown user");
                            }
                        })
                        .addOnFailureListener(e -> holder.tvUserEmail.setText("Error"));
            }
        } else {
            holder.tvUserEmail.setText("Unknown user");
        }

        // 2️⃣ Hitung item dalam transaksi
        List<CartItem> items = trx.getItems();
        int distinctCount = items == null ? 0 : items.size();
        int totalQty = 0;
        if (items != null) {
            for (CartItem ci : items) totalQty += ci.getQuantity();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String tsText = trx.getTimestamp() <= 0 ? "-" : sdf.format(new Date(trx.getTimestamp()));
        holder.tvTime.setText(tsText);

        // 3️⃣ Ambil produk pertama untuk tampilan representative
        if (items != null && !items.isEmpty()) {
            CartItem first = items.get(0);
            if (first != null && first.getFoodId() != null) {
                db.collection("foods").document(first.getFoodId()).get()
                        .addOnSuccessListener(doc -> {
                            if (!doc.exists()) {
                                holder.tvTransName.setText("Unknown product" + (distinctCount > 1 ? " +" + (distinctCount - 1) + " items" : ""));
                                holder.ivTransImage.setImageResource(R.drawable.food);
                                return;
                            }

                            Food f = doc.toObject(Food.class);
                            if (f == null) {
                                holder.tvTransName.setText("Unknown product");
                                holder.ivTransImage.setImageResource(R.drawable.food);
                                return;
                            }

                            String title = f.getName() == null ? "-" : f.getName();
                            if (distinctCount > 1) {
                                holder.tvTransName.setText(title + " +" + (distinctCount - 1) + " items");
                            } else {
                                holder.tvTransName.setText(title);
                            }

                            holder.tvId.setText("ID: " + trx.getId());
                            holder.tvDetailClick.setText("Click to see details");

                            if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
                                Glide.with(ctx).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivTransImage);
                            } else {
                                holder.ivTransImage.setImageResource(R.drawable.food);
                            }

                            // 🔹 Ambil exp date terdekat
                            db.collection("food_exp_date_stocks")
                                    .whereEqualTo("foodId", f.getId())
                                    .get()
                                    .addOnSuccessListener(q -> {
                                        Date nearest = null;
                                        for (QueryDocumentSnapshot sDoc : q) {
                                            FoodExpDateStock s = sDoc.toObject(FoodExpDateStock.class);
                                            Date exp = s.getExp_date();
                                            if (exp != null && (nearest == null || exp.before(nearest))) {
                                                nearest = exp;
                                            }
                                        }
                                        if (nearest != null) {
                                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                            holder.tvTransExp.setText("Exp date: " + sdf2.format(nearest));
                                        } else {
                                            holder.tvTransExp.setText("Exp date: -");
                                        }
                                    })
                                    .addOnFailureListener(e -> holder.tvTransExp.setText("Exp date: -"));
                        })
                        .addOnFailureListener(e -> {
                            holder.ivTransImage.setImageResource(R.drawable.food);
                            holder.tvTransName.setText("Unknown product");
                            holder.tvTransExp.setText("Exp date: -");
                        });
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(ctx, TransactionDetailActivity.class);
            i.putExtra("transactionId", trx.getId());
            ctx.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivTransImage;
        TextView tvId, tvTransName, tvDetailClick, tvTransExp, tvTransTotal, tvTime, tvUserEmail;

        VH(@NonNull View itemView) {
            super(itemView);
            ivTransImage = itemView.findViewById(R.id.ivTransImage);
            tvId = itemView.findViewById(R.id.tvId);
            tvTransName = itemView.findViewById(R.id.tvTransName);
            tvDetailClick = itemView.findViewById(R.id.tvDetailClick);
            tvTransExp = itemView.findViewById(R.id.tvTransExp);
            tvTransTotal = itemView.findViewById(R.id.tvTransTotal);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
        }
    }
}
