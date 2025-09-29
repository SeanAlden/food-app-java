package com.example.foodapp_java.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Supplier;

import java.io.File;
import java.util.List;

public class AdapterSupplier extends RecyclerView.Adapter<AdapterSupplier.VH> {

    public interface OnItemClickListener {
        void onItemClick(Supplier supplier);
    }

    private Context ctx;
    private List<Supplier> list;
    private OnItemClickListener listener;

    public AdapterSupplier(Context ctx, List<Supplier> list, OnItemClickListener listener){
        this.ctx = ctx;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_supplier, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Supplier s = list.get(position);
        holder.tvName.setText(s.getName() == null ? "-" : s.getName());
        holder.tvCode.setText(s.getCode() == null ? "-" : s.getCode());
        holder.tvPhone.setText(s.getCode() == null ? "-" : s.getPhone());
        holder.tvAddress.setText(s.getCode() == null ? "-" : s.getAddress());

        // load image from local path or fallback
        String path = s.getImage();
        if (path != null && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                holder.img.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
            } else {
                holder.img.setImageResource(R.drawable.supplier);
            }
        } else {
            holder.img.setImageResource(R.drawable.supplier);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(s);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvCode, tvPhone, tvAddress;
        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgSupplier);
            tvName = itemView.findViewById(R.id.tvSupplierName);
            tvCode = itemView.findViewById(R.id.tvSupplierCode);
            tvPhone = itemView.findViewById(R.id.tvSupplierPhone);
            tvAddress = itemView.findViewById(R.id.tvSupplierAddress);
        }
    }
}
