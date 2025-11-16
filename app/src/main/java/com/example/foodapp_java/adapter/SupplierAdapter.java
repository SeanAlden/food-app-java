//package com.example.foodapp_java.adapter;
//
//import android.content.Context;
//import android.graphics.BitmapFactory;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.dataClass.Supplier;
//
//import java.io.File;
//import java.util.List;
//
//public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.VH> {
//
//    public interface OnItemClickListener {
//        void onItemClick(Supplier supplier);
//    }
//
//    private Context ctx;
//    private List<Supplier> list;
//    private OnItemClickListener listener;
//
//    public SupplierAdapter(Context ctx, List<Supplier> list, OnItemClickListener listener){
//        this.ctx = ctx;
//        this.list = list;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(ctx).inflate(R.layout.item_supplier, parent, false);
//        return new VH(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull VH holder, int position) {
//        Supplier s = list.get(position);
//        holder.tvName.setText(s.getName() == null ? "-" : s.getName());
//        holder.tvCode.setText(s.getCode() == null ? "-" : s.getCode());
//        holder.tvPhone.setText(s.getCode() == null ? "-" : s.getPhone());
//        holder.tvAddress.setText(s.getCode() == null ? "-" : s.getAddress());
//
//        // load image from local path or fallback
//        String path = s.getImage();
//        if (path != null && !path.isEmpty()) {
//            File f = new File(path);
//            if (f.exists()) {
//                holder.img.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
//            } else {
//                holder.img.setImageResource(R.drawable.supplier);
//            }
//        } else {
//            holder.img.setImageResource(R.drawable.supplier);
//        }
//
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) listener.onItemClick(s);
//        });
//    }
//
//    @Override
//    public int getItemCount() { return list.size(); }
//
//    static class VH extends RecyclerView.ViewHolder {
//        ImageView img;
//        TextView tvName, tvCode, tvPhone, tvAddress;
//        VH(@NonNull View itemView) {
//            super(itemView);
//            img = itemView.findViewById(R.id.imgSupplier);
//            tvName = itemView.findViewById(R.id.tvSupplierName);
//            tvCode = itemView.findViewById(R.id.tvSupplierCode);
//            tvPhone = itemView.findViewById(R.id.tvSupplierPhone);
//            tvAddress = itemView.findViewById(R.id.tvSupplierAddress);
//        }
//    }
//}

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
import com.example.foodapp_java.dataClass.Supplier;

import java.io.File;
import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.VH> {

    public interface SupplierListener {
        void onEdit(Supplier supplier);

        void onDelete(Supplier supplier);

        void onClick(Supplier supplier);
    }

    private Context ctx;
    private List<Supplier> list;
    private SupplierListener listener;

    public SupplierAdapter(Context ctx, List<Supplier> list, SupplierListener listener) {
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

//    @Override
//    public void onBindViewHolder(@NonNull VH holder, int position) {
//        Supplier s = list.get(position);
//
//        holder.tvName.setText(s.getName());
//        holder.tvCode.setText("Code: " + s.getCode());
//        holder.tvPhone.setText("Phone: " + s.getPhone());
//        holder.tvAddress.setText("Address: " + s.getAddress());
//
//        String path = s.getImage();
//        if (path != null && !path.isEmpty()) {
//            File f = new File(path);
//            if (f.exists()) holder.img.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
//            else holder.img.setImageResource(R.drawable.supplier);
//        } else {
//            holder.img.setImageResource(R.drawable.supplier);
//        }
//
//        holder.btnEdit.setOnClickListener(v -> listener.onEdit(s));
//        holder.btnDelete.setOnClickListener(v -> listener.onDelete(s));
//        holder.itemView.setOnClickListener(v -> listener.onClick(s));
//    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Supplier s = list.get(position);

        holder.tvName.setText(s.getName());
        holder.tvCode.setText("Code: " + s.getCode());
        holder.tvPhone.setText("Phone: " + s.getPhone());
        holder.tvAddress.setText("Address: " + s.getAddress());

        // --- LOAD IMAGE ---
        String path = s.getImage();
        if (path != null && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists())
                holder.img.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
            else holder.img.setImageResource(R.drawable.supplier);
        } else holder.img.setImageResource(R.drawable.supplier);

        // ============================
        // 🔥 DISABLE DELETE IF RELATED
        // ============================
        if (!s.isCanDelete()) {
            holder.btnDelete.setEnabled(false);
            holder.btnDelete.setAlpha(0.4f);   // Greys out
            holder.btnDelete.setBackgroundTintList(
                    ctx.getResources().getColorStateList(android.R.color.darker_gray)
            );
        } else {
            holder.btnDelete.setEnabled(true);
            holder.btnDelete.setAlpha(1f);
            holder.btnDelete.setBackgroundTintList(
                    ctx.getResources().getColorStateList(android.R.color.holo_red_dark)
            );
        }

        holder.btnEdit.setOnClickListener(v -> listener.onEdit(s));
        holder.btnDelete.setOnClickListener(v -> {
            if (s.isCanDelete()) listener.onDelete(s);
        });

        holder.itemView.setOnClickListener(v -> listener.onClick(s));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvCode, tvPhone, tvAddress;
        Button btnEdit, btnDelete;

        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgSupplier);
            tvName = itemView.findViewById(R.id.tvSupplierName);
            tvCode = itemView.findViewById(R.id.tvSupplierCode);
            tvPhone = itemView.findViewById(R.id.tvSupplierPhone);
            tvAddress = itemView.findViewById(R.id.tvSupplierAddress);
            btnEdit = itemView.findViewById(R.id.btnItemEdit);
            btnDelete = itemView.findViewById(R.id.btnItemDelete);
        }
    }
}
