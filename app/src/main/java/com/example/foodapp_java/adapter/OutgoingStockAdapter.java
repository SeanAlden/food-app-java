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

import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.OutgoingStock; // Diubah

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OutgoingStockAdapter extends RecyclerView.Adapter<OutgoingStockAdapter.VH> {

    public interface OnActionListener {
        void onEdit(OutgoingStock entry);
        void onDelete(OutgoingStock entry);
    }

    private Context ctx;
    private List<OutgoingStock> list;
    private OnActionListener listener;

    public OutgoingStockAdapter(Context ctx, List<OutgoingStock> list, OnActionListener listener) {
        this.ctx = ctx;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_outgoing_stock, parent, false); // Layout baru
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OutgoingStock e = list.get(position);

        holder.tvFood.setText(e.getFoodName() == null ? "-" : e.getFoodName());
        holder.tvCategory.setText("Category: " + (e.getCategoryName() == null ? "-" : e.getCategoryName()));
//        holder.tvDestination.setText("By: " + (e.getOperatorName() == null ? "-" : e.getOperatorName())); // Disederhanakan, karena tidak ada supplier
        holder.tvSupplier.setText("Supplier: " + (e.getSupplierName() == null ? "-" : e.getSupplierName())
                + " — By: " + (e.getOperatorName() == null ? "-" : e.getOperatorName()));

        String exp = e.getExp_date() == null ? "-" : new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(e.getExp_date());
        holder.tvQty.setText("Exp: " + exp + " — Removed: " + e.getQty()); // Diubah "Added" menjadi "Removed"

        if (e.getImagePath() != null && !e.getImagePath().isEmpty()) {
            holder.img.setImageBitmap(BitmapFactory.decodeFile(e.getImagePath()));
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

    public void setList(List<OutgoingStock> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvFood, tvCategory, tvQty, tvDestination, tvSupplier;
        Button btnEdit, btnDelete;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgFoodSmallOutgoing);
            tvFood = itemView.findViewById(R.id.tvFoodNameOutgoing);
            tvCategory = itemView.findViewById(R.id.tvCategoryOutgoing);
            tvQty = itemView.findViewById(R.id.tvQtyOutgoing);
            tvSupplier = itemView.findViewById(R.id.tvSupplierOutgoing);
//            tvDestination = itemView.findViewById(R.id.tvDestinationOutgoing);
            btnEdit = itemView.findViewById(R.id.btnEditOutgoing);
            btnDelete = itemView.findViewById(R.id.btnDeleteOutgoing);
        }
    }
}