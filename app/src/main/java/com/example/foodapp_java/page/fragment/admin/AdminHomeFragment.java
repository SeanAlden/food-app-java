//package com.example.foodapp_java.page.fragment.admin;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.adapter.NewsAdapter;
//import com.example.foodapp_java.page.CategoryActivity;
//import com.example.foodapp_java.page.CustomerActivity;
//import com.example.foodapp_java.page.FavoriteActivity;
//import com.example.foodapp_java.page.FoodActivity;
//import com.example.foodapp_java.page.LoginActivity;
//import com.example.foodapp_java.page.StockManagementActivity;
//import com.example.foodapp_java.page.SupplierActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//
//public class AdminHomeFragment extends Fragment {
//
//    private TextView tvHeader;
//    private FirebaseAuth auth;
//    private FirebaseUser user;
//    private FirebaseFirestore db;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
//
//        // Setup Toolbar
//        Toolbar toolbar = view.findViewById(R.id.toolbarAdminHome);
//        toolbar.setTitle("");
//
//        // Firebase init
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        user = auth.getCurrentUser();
//
//        // Header
//        tvHeader = view.findViewById(R.id.tvHeader);
//
//        if (user != null) {
//            db.collection("users").document(user.getUid())
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            String name = documentSnapshot.getString("name");
//                            if (name != null && !name.isEmpty()) {
//                                tvHeader.setText("Hi, " + name);
//                            } else {
//                                tvHeader.setText("Hi, " + user.getEmail());
//                            }
//                        } else {
//                            tvHeader.setText("Hi, " + user.getEmail());
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
//                        tvHeader.setText("Hi, " + user.getEmail());
//                    });
//        }
//
//        // Tombol navigasi
//        ImageButton btnFood = view.findViewById(R.id.btnFood);
//        ImageButton btnCategory = view.findViewById(R.id.btnCategory);
//        ImageButton btnCustomer = view.findViewById(R.id.btnCustomer);
//        ImageButton btnFavorite = view.findViewById(R.id.btnFavorite);
//        ImageButton btnSupplier = view.findViewById(R.id.btnSupplier);
//        ImageButton btnStockManagement = view.findViewById(R.id.btnStockManagement);
//
//        // Dummy data toko
//        TextView tvTotalFoods = view.findViewById(R.id.tvTotalFoods);
//        TextView tvTotalTransactions = view.findViewById(R.id.tvTotalTransactions);
//        TextView tvTotalCustomers = view.findViewById(R.id.tvTotalCustomers);
//
//        tvTotalFoods.setText("5");
//        tvTotalTransactions.setText("12");
//        tvTotalCustomers.setText("8");
//
//        btnFood.setOnClickListener(v -> startActivity(new Intent(getActivity(), FoodActivity.class)));
//        btnCategory.setOnClickListener(v -> startActivity(new Intent(getActivity(), CategoryActivity.class)));
//        btnCustomer.setOnClickListener(v -> startActivity(new Intent(getActivity(), CustomerActivity.class)));
//        btnFavorite.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavoriteActivity.class)));
//        btnStockManagement.setOnClickListener(v -> startActivity(new Intent(getActivity(), StockManagementActivity.class)));
//        btnSupplier.setOnClickListener(v -> startActivity(new Intent(getActivity(), SupplierActivity.class)));
//
//        // RecyclerView setup
//        RecyclerView rvNews = view.findViewById(R.id.recyclerNews);
//        rvNews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        ArrayList<Integer> images = new ArrayList<>();
//        images.add(R.drawable.food);
//        images.add(R.drawable.food);
//        images.add(R.drawable.food);
//
//        NewsAdapter adapter = new NewsAdapter(images);
//        rvNews.setAdapter(adapter);
//
//        return view;
//    }
//}

package com.example.foodapp_java.page.fragment.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.NewsAdapter;
import com.example.foodapp_java.api.NewsResponse;
import com.example.foodapp_java.api.NewsService;
import com.example.foodapp_java.api.RetrofitClient;
import com.example.foodapp_java.dataClass.Article;
import com.example.foodapp_java.page.CategoryActivity;
import com.example.foodapp_java.page.CustomerActivity;
import com.example.foodapp_java.page.FavoriteActivity;
import com.example.foodapp_java.page.FoodActivity;
import com.example.foodapp_java.page.NewsDetailActivity;
import com.example.foodapp_java.page.StockManagementActivity;
import com.example.foodapp_java.page.SupplierActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHomeFragment extends Fragment {

    private TextView tvHeader;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    private NewsAdapter newsAdapter;
    private List<Article> articleList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Setup Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbarAdminHome);
        toolbar.setTitle("");

        // Firebase init
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // Header
        tvHeader = view.findViewById(R.id.tvHeader);

        if (user != null) {
//            db.collection("users").document(user.getUid())
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            String name = documentSnapshot.getString("name");
//                            if (name != null && !name.isEmpty()) {
//                                tvHeader.setText("Hi, " + name);
//                            } else {
//                                tvHeader.setText("Hi, " + user.getEmail());
//                            }
//                        } else {
//                            tvHeader.setText("Hi, " + user.getEmail());
//                        }
//                    })

            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            if (name != null && !name.isEmpty()) {
                                tvHeader.setText("Hi, " + name);
                            } else {
                                tvHeader.setText("Hi, " + user.getEmail());
                            }

                            String profileUrl = documentSnapshot.getString("profileUrl");
                            ImageView imgProfile = view.findViewById(R.id.imgProfile); // pastikan 'view' dalam scope atau simpan sebelumnya
                            if (profileUrl != null && !profileUrl.isEmpty()) {
                                File f = new File(profileUrl);
                                if (f.exists()) {
                                    Glide.with(getContext()).load(f).circleCrop().into(imgProfile);
                                } else {
                                    Glide.with(getContext()).load(R.drawable.profile).circleCrop().into(imgProfile);
                                }
                            } else {
                                Glide.with(getContext()).load(R.drawable.profile).circleCrop().into(imgProfile);
                            }
                        } else {
                            tvHeader.setText("Hi, " + user.getEmail());
                        }
                    })

                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                        tvHeader.setText("Hi, " + user.getEmail());
                    });
        }

        // Tombol navigasi
        ImageButton btnFood = view.findViewById(R.id.btnFood);
        ImageButton btnCategory = view.findViewById(R.id.btnCategory);
        ImageButton btnCustomer = view.findViewById(R.id.btnCustomer);
        ImageButton btnFavorite = view.findViewById(R.id.btnFavorite);
        ImageButton btnSupplier = view.findViewById(R.id.btnSupplier);
        ImageButton btnStockManagement = view.findViewById(R.id.btnStockManagement);

        // Dummy data toko
        TextView tvTotalFoods = view.findViewById(R.id.tvTotalFoods);
        TextView tvTotalTransactions = view.findViewById(R.id.tvTotalTransactions);
        TextView tvTotalCustomers = view.findViewById(R.id.tvTotalCustomers);

        tvTotalFoods.setText("5");
        tvTotalTransactions.setText("12");
        tvTotalCustomers.setText("8");

        btnFood.setOnClickListener(v -> startActivity(new Intent(getActivity(), FoodActivity.class)));
        btnCategory.setOnClickListener(v -> startActivity(new Intent(getActivity(), CategoryActivity.class)));
        btnCustomer.setOnClickListener(v -> startActivity(new Intent(getActivity(), CustomerActivity.class)));
        btnFavorite.setOnClickListener(v -> startActivity(new Intent(getActivity(), FavoriteActivity.class)));
        btnStockManagement.setOnClickListener(v -> startActivity(new Intent(getActivity(), StockManagementActivity.class)));
        btnSupplier.setOnClickListener(v -> startActivity(new Intent(getActivity(), SupplierActivity.class)));

        // RecyclerView setup
        RecyclerView rvNews = view.findViewById(R.id.recyclerNews);
        rvNews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        newsAdapter = new NewsAdapter(getContext(), articleList, article -> {
            Intent i = new Intent(getActivity(), NewsDetailActivity.class);
            i.putExtra("article", article);
            startActivity(i);
        });
        rvNews.setAdapter(newsAdapter);

        fetchNews();

        return view;
    }

    private void fetchNews() {
        String apiKey = getString(R.string.news_api_key);
        NewsService service = RetrofitClient.getClient().create(NewsService.class);

        service.searchArticles("food shop", 10, apiKey, "en")
                .enqueue(new Callback<NewsResponse>() {
                    @Override
                    public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Article> list = response.body().getArticles();
                            if (list != null && !list.isEmpty()) {
                                articleList.clear();
                                articleList.addAll(list);
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> newsAdapter.notifyDataSetChanged());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsResponse> call, Throwable t) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Failed to load news", Toast.LENGTH_SHORT).show();
                        }
                        t.printStackTrace();
                    }
                });
    }
}
