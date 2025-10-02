package com.example.foodapp_java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.firebaseauth.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.User;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Context context;
    private List<User> userList;

    public CustomerAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User user = userList.get(position);
        holder.txtName.setText(user.getName());
        holder.txtEmail.setText(user.getEmail());
        holder.txtPhone.setText(user.getPhone());

        // ✅ Load gambar jika ada, fallback ke default
        if (user.getProfileUrl() != null && !user.getProfileUrl().isEmpty()) {
            Glide.with(context)
                    .load(user.getProfileUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.profile) // tampil saat loading
                            .error(R.drawable.profile)       // tampil jika gagal load
                            .circleCrop())                   // lingkaran
                    .into(holder.imgProfile);
        } else {
            holder.imgProfile.setImageResource(R.drawable.profile);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView txtName, txtEmail, txtPhone;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtName = itemView.findViewById(R.id.txtName);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtPhone = itemView.findViewById(R.id.txtPhone);
        }
    }
}
