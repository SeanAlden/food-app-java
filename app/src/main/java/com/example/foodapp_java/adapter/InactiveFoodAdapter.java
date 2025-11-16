package com.example.foodapp_java.adapter;

import android.app.AlertDialog;
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
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InactiveFoodAdapter extends RecyclerView.Adapter<InactiveFoodAdapter.VH> {

    private Context ctx;
    private List<Food> list;

    public InactiveFoodAdapter(Context ctx, List<Food> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_inactive_food, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Food f = list.get(position);

        holder.tvName.setText(f.getName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Rp %.0f", f.getPrice()));
        holder.tvDesc.setText(f.getDescription());

        if (f.getNearestExpDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            holder.tvExp.setText("Nearest exp: " + sdf.format(f.getNearestExpDate()) + " (stock: " + f.getTotalStock() + ")");
        } else {
            holder.tvExp.setText("Nearest exp: - (stock: " + f.getTotalStock() + ")");
        }

        if (f.getImagePath() != null) {
            File img = new File(f.getImagePath());
            if (img.exists()) {
                holder.imgFood.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));
            } else holder.imgFood.setImageResource(R.drawable.food);
        }

        holder.btnActivate.setOnClickListener(v -> showConfirmDialog(f, position));
    }

    private void showConfirmDialog(Food f, int position) {
        new AlertDialog.Builder(ctx)
                .setTitle("Aktifkan Makanan?")
                .setMessage("Apakah Anda yakin ingin mengaktifkan \"" + f.getName() + "\"?")
                .setPositiveButton("Ya", (dialog, which) -> activateFood(f, position))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void activateFood(Food f, int position) {
        FirebaseFirestore.getInstance()
                .collection("foods")
                .document(f.getId())
                .update("status", "active")
                .addOnSuccessListener(unused -> {
                    list.remove(position);
                    notifyItemRemoved(position);
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        ImageView imgFood;
        TextView tvName, tvPrice, tvExp, tvDesc, tvCategory;
        Button btnActivate;

        public VH(@NonNull View v) {
            super(v);
            imgFood = v.findViewById(R.id.imgFood);
            tvName = v.findViewById(R.id.tvFoodName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvExp = v.findViewById(R.id.tvStockExp);
            tvDesc = v.findViewById(R.id.tvDesc);
            tvCategory = v.findViewById(R.id.tvCategory);
            btnActivate = v.findViewById(R.id.btnActivate);
        }
    }
}
