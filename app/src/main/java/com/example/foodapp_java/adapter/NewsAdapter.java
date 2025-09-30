//package com.example.foodapp_java.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
////import com.example.firebaseauth.R;
//import com.example.foodapp_java.R;
//
//import java.util.ArrayList;
//
//public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
//
//    private ArrayList<Integer> images;
//
//    public NewsAdapter(ArrayList<Integer> images) {
//        this.images = images;
//    }
//
//    @NonNull
//    @Override
//    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull NewsAdapter.ViewHolder holder, int position) {
//        holder.imgNews.setImageResource(images.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return images.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView imgNews;
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imgNews = itemView.findViewById(R.id.imgNews);
//        }
//    }
//}

// app/src/main/java/com/example/foodapp_java/adapter/NewsAdapter.java
package com.example.foodapp_java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodapp_java.R;
import com.bumptech.glide.Glide;
import com.example.foodapp_java.dataClass.Article;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.VH> {

    public interface OnItemClick {
        void onClick(Article article);
    }

    private Context ctx;
    public List<Article> list;
    private OnItemClick listener;

    public NewsAdapter(Context ctx, List<Article> list, OnItemClick listener) {
        this.ctx = ctx;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_news, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Article a = list.get(position);
        holder.tvTitle.setText(a.getTitle() == null ? "-" : a.getTitle());

        String img = a.getUrlToImage();
        Glide.with(ctx)
                .load(img)
                .centerCrop()
                .placeholder(R.drawable.food)
                .error(R.drawable.food)
                .into(holder.img);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(a);
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvTitle;
        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgNews);
            tvTitle = itemView.findViewById(R.id.tvNewsTitle);
        }
    }
}
