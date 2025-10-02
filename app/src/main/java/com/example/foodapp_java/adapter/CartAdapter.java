package com.example.foodapp_java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.CartItem;
import com.example.foodapp_java.dataClass.Food;
import com.example.foodapp_java.dataClass.FoodExpDateStock;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    private Context ctx;
    private List<CartItem> list;
    private FirebaseFirestore db;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onTotalChanged(double newTotal);
    }

    public CartAdapter(Context ctx, List<CartItem> list, OnCartChangeListener listener) {
        this.ctx = ctx;
        this.list = list;
        this.db = FirebaseFirestore.getInstance();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.VH holder, int position) {
        CartItem ci = list.get(position);

        holder.tvQty.setText(String.valueOf(ci.getQuantity()));

        // load food doc
        db.collection("foods").document(ci.getFoodId()).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;
                    Food f = doc.toObject(Food.class);
                    if (f == null) return;

                    holder.tvName.setText(f.getName() == null ? "-" : f.getName());
                    holder.tvPrice.setText("Rp " + f.getPrice());

                    if (f.getImagePath() != null && !f.getImagePath().isEmpty()) {
                        Glide.with(ctx).load(f.getImagePath()).placeholder(R.drawable.food).into(holder.ivImage);
                    } else {
                        holder.ivImage.setImageResource(R.drawable.food);
                    }

                    // compute subtotal
                    double subtotal = f.getPrice() * ci.getQuantity();
                    holder.tvSubtotal.setText("Subtotal: Rp " + (long) subtotal);

                    // fetch stock info (nearest exp + total stock)
                    db.collection("food_exp_date_stocks")
                            .whereEqualTo("foodId", f.getId())
                            .get()
                            .addOnSuccessListener(q -> {
                                int totalStock = 0;
                                Date nearest = null;
                                for (QueryDocumentSnapshot sDoc : q) {
                                    FoodExpDateStock s = sDoc.toObject(FoodExpDateStock.class);
                                    totalStock += s.getStock_amount();
                                    Date exp = s.getExp_date();
                                    if (exp != null && (nearest == null || exp.before(nearest))) {
                                        nearest = exp;
                                    }
                                }
                                holder.tvStock.setText("Total stock: " + totalStock);
                                if (nearest != null) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    holder.tvExp.setText("Exp: " + sdf.format(nearest));
                                } else {
                                    holder.tvExp.setText("Exp: -");
                                }

                                // notify total changed upward (calling activity will re-calc total)
                                recalcTotal();
                            })
                            .addOnFailureListener(e -> {
                                holder.tvStock.setText("Total stock: -");
                                holder.tvExp.setText("Exp: -");
                                recalcTotal();
                            });
                });

        // delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (ci.getId() == null) return;
            db.collection("carts").document(ci.getId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ctx, "Removed from cart", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ctx, "Failed remove", Toast.LENGTH_SHORT).show());
        });

        // minus
        holder.btnMinus.setOnClickListener(v -> {
            if (ci.getQuantity() <= 1) {
                // consider deleting or disallowing less than 1; we disallow <1
                Toast.makeText(ctx, "Minimum quantity is 1", Toast.LENGTH_SHORT).show();
                return;
            }
            int newQty = ci.getQuantity() - 1;
            db.collection("carts").document(ci.getId()).update("quantity", newQty)
                    .addOnSuccessListener(aVoid -> {
                        // local will update via snapshot listener in activity
                    })
                    .addOnFailureListener(e -> Toast.makeText(ctx, "Failed update quantity", Toast.LENGTH_SHORT).show());
        });

        // plus
//        holder.btnPlus.setOnClickListener(v -> {
//            // We don't check stock here (checkout will validate). Optionally you can check total stock and disallow
//            int newQty = ci.getQuantity() + 1;
//            db.collection("carts").document(ci.getId()).update("quantity", newQty)
//                    .addOnSuccessListener(aVoid -> {
//                        // updated via snapshot listener
//                    })
//                    .addOnFailureListener(e -> Toast.makeText(ctx, "Failed update quantity", Toast.LENGTH_SHORT).show());
//        });

        // plus
        holder.btnPlus.setOnClickListener(v -> {
            // Ambil total stock dulu sebelum update
            db.collection("food_exp_date_stocks")
                    .whereEqualTo("foodId", ci.getFoodId())
                    .get()
                    .addOnSuccessListener(q -> {
                        int totalStock = 0;
                        for (QueryDocumentSnapshot sDoc : q) {
                            FoodExpDateStock s = sDoc.toObject(FoodExpDateStock.class);
                            totalStock += s.getStock_amount();
                        }

                        if (ci.getQuantity() >= totalStock) {
                            Toast.makeText(ctx, "Stock tidak mencukupi", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int newQty = ci.getQuantity() + 1;
                        db.collection("carts").document(ci.getId()).update("quantity", newQty)
                                .addOnSuccessListener(aVoid -> {
                                    // update via snapshot listener otomatis
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(ctx, "Gagal update quantity", Toast.LENGTH_SHORT).show()
                                );
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ctx, "Gagal cek stok", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void recalcTotal() {
        // let activity recompute by reading current list + food prices again
        // safer approach: ask activity to recompute (the adapter cannot access all foods easily)
        if (listener != null) listener.onTotalChanged(0);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<CartItem> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvSubtotal, tvExp, tvStock, tvQty;
        ImageButton btnDelete, btnMinus, btnPlus;

        public VH(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivCartImage);
            tvName = itemView.findViewById(R.id.tvCartFoodName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvSubtotal = itemView.findViewById(R.id.tvCartSubtotal);
            tvExp = itemView.findViewById(R.id.tvCartExp);
            tvStock = itemView.findViewById(R.id.tvCartStock);
            tvQty = itemView.findViewById(R.id.tvCartQty);
            btnDelete = itemView.findViewById(R.id.btnDeleteCart);
            btnMinus = itemView.findViewById(R.id.btnCartMinus);
            btnPlus = itemView.findViewById(R.id.btnCartPlus);
        }
    }
}
