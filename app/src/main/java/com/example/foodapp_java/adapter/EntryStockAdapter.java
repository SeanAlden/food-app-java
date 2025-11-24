package com.example.foodapp_java.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.EntryStock;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EntryStockAdapter extends RecyclerView.Adapter<EntryStockAdapter.VH> {

    public interface OnActionListener {
        void onEdit(EntryStock entry);

        void onDelete(EntryStock entry);
    }

    private Context ctx;
    private List<EntryStock> list;
    private OnActionListener listener;

    public EntryStockAdapter(Context ctx, List<EntryStock> list, OnActionListener listener) {
        this.ctx = ctx;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_entry_stock, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        EntryStock e = list.get(position);

        holder.tvFood.setText(e.getFoodName() == null ? "-" : e.getFoodName());
        holder.tvCategory.setText("Category: " + (e.getCategoryName() == null ? "-" : e.getCategoryName()));
        holder.tvSupplier.setText("Supplier: " + (e.getSupplierName() == null ? "-" : e.getSupplierName())
                + " — By: " + (e.getOperatorName() == null ? "-" : e.getOperatorName()));

        String exp = e.getExp_date() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(e.getExp_date());
        holder.tvQty.setText("Exp: " + exp + " — Added: " + e.getQty());


//        if (e.getImagePath() != null && !e.getImagePath().isEmpty()) {
//            holder.img.setImageBitmap(BitmapFactory.decodeFile(e.getImagePath()));
//        } else {
//            holder.img.setImageResource(R.drawable.food);
//        }

        if (e.getImagePath() != null && !e.getImagePath().isEmpty()) {
            Glide.with(ctx)
                    .load(e.getImagePath())
                    .placeholder(R.drawable.food)
                    .error(R.drawable.food)
                    .into(holder.img);
        } else {
            holder.img.setImageResource(R.drawable.food);
        }


        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(e);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(e);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<EntryStock> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvFood, tvCategory, tvQty, tvSupplier;
        Button btnEdit, btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgFoodSmall);
            tvFood = itemView.findViewById(R.id.tvFoodNameEntry);
            tvCategory = itemView.findViewById(R.id.tvCategoryEntry);
            tvQty = itemView.findViewById(R.id.tvQtyEntry);
            tvSupplier = itemView.findViewById(R.id.tvSupplierEntry);
            btnEdit = itemView.findViewById(R.id.btnEditEntry);
            btnDelete = itemView.findViewById(R.id.btnDeleteEntry);
        }
    }
}
