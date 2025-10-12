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
import java.util.List;
import java.util.Locale;

public class TransactionListAdapter extends RecyclerView.Adapter<TransactionListAdapter.VH> {
    private final Context ctx;
    private List<TransactionOffline> list;
    private final FirebaseFirestore db;

    public TransactionListAdapter(Context ctx, List<TransactionOffline> list) {
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
    public TransactionListAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_transaction, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListAdapter.VH holder, int position) {
        TransactionOffline trx = list.get(position);

        // default values
        holder.tvTransTotal.setText("Total: Rp " + (long) trx.getTotalPrice());
        holder.tvTransExp.setText("Exp date: -");
        holder.tvTransName.setText("Transaction #" + trx.getId());

        // compute total distinct items and total quantity
        List<CartItem> items = trx.getItems();
        int distinctCount = items == null ? 0 : items.size();
        int totalQty = 0;
        if (items != null) {
            for (CartItem ci : items) totalQty += ci.getQuantity();
        }

        // set header (id + time) — time formatted
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String tsText = trx.getTimestamp() <= 0 ? "-" : sdf.format(new Date(trx.getTimestamp()));
        holder.itemView.setContentDescription("Transaction at " + tsText);

        // if there is at least one item, use its food doc as representative
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
                                holder.tvTransName.setText("Unknown product" + (distinctCount > 1 ? " +" + (distinctCount - 1) + " items" : ""));
                                holder.ivTransImage.setImageResource(R.drawable.food);
                                return;
                            }

                            String title = f.getName() == null ? "-" : f.getName();
                            if (distinctCount > 1) {
                                holder.tvTransName.setText(title);
                            }

                            holder.tvDetailClick.setText("Click to see " + "+" + (distinctCount - 1) + " items");

                            holder.tvId.setText("ID: " + String.valueOf(trx.getId()));

                            holder.tvTime.setText(tsText);

                            if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
                                Glide.with(ctx).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivTransImage);
                            } else {
                                holder.ivTransImage.setImageResource(R.drawable.food);
                            }

                            // find nearest exp for this representative food
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
                            holder.tvTransName.setText("Unknown product" + (distinctCount > 1 ? " +" + (distinctCount - 1) + " items" : ""));
                            holder.tvTransExp.setText("Exp date: -");
                        });
            }
        }

        // set total and time (trans id + time at top left)
        SimpleDateFormat sdfHeader = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        String header = "#" + trx.getId() + "   " + (trx.getTimestamp() > 0 ? sdfHeader.format(new Date(trx.getTimestamp())) : "-");
        // we intentionally reuse tvTransName to show product info; put header into contentDescription or tag
        holder.itemView.setTag(header);

        // show total price (already set) and also append total qty to <tvTransTotal> or another way:
        holder.tvTransTotal.setText("Total: Rp " + (long) trx.getTotalPrice());

        // click to open TransactionDetailActivity
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
        TextView tvId, tvTransName, tvDetailClick, tvTransExp, tvTransTotal, tvTime;

        VH(@NonNull View itemView) {
            super(itemView);
            ivTransImage = itemView.findViewById(R.id.ivTransImage);
            tvId = itemView.findViewById(R.id.tvId);
            tvTransName = itemView.findViewById(R.id.tvTransName);
            tvDetailClick = itemView.findViewById(R.id.tvDetailClick);
            tvTransExp = itemView.findViewById(R.id.tvTransExp);
            tvTransTotal = itemView.findViewById(R.id.tvTransTotal);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
