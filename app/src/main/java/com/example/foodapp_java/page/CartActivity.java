package com.example.foodapp_java.page;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.CartAdapter;
import com.example.foodapp_java.dataBaseOffline.TransactionOffline;
import com.example.foodapp_java.dataBaseOffline.TransactionOfflineDatabase;
import com.example.foodapp_java.dataClass.CartItem;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private TextView tvTotal;
    private Button btnCheckout;
    private CartAdapter adapter;
    private List<CartItem> cartList = new ArrayList<>();
    private long currentTotalAmount = 0;

    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbarCart);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvCart = findViewById(R.id.rvCart);
        tvTotal = findViewById(R.id.tvTotal);
        btnCheckout = findViewById(R.id.btnCheckout);

        rvCart.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(this, cartList, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onTotalChanged(double newTotal) {
                // Trigger recompute
                computeTotal();
            }
        });
        rvCart.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid == null) {
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listenCartRealtime();

        // Checkout button disabled for now (you asked show only)
//        btnCheckout.setEnabled(false);

        btnCheckout.setEnabled(true);
        btnCheckout.setOnClickListener(v -> performCheckout());
    }

    private void listenCartRealtime() {
        db.collection("carts")
                .whereEqualTo("userId", uid)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        e.printStackTrace();
                        return;
                    }
                    if (snap == null) return;

                    cartList.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        CartItem ci = d.toObject(CartItem.class);
                        if (ci != null) {
                            ci.setId(d.getId());
                            cartList.add(ci);
                        }
                    }
                    adapter.setList(cartList);
                    computeTotal();
                });
    }

    private void computeTotal() {
        // fetch food prices for each cart item and sum
        if (cartList.isEmpty()) {
            tvTotal.setText("Total: Rp 0");
            this.currentTotalAmount = 0;
            return;
        }

        final long[] total = {0L};
        final int[] processed = {0};
        for (CartItem ci : cartList) {
            db.collection("foods").document(ci.getFoodId()).get()
                    .addOnSuccessListener(doc -> {
                        processed[0]++;
                        if (doc.exists()) {
                            Double price = doc.getDouble("price");
                            long p = price == null ? 0L : Math.round(price);
                            total[0] += p * ci.getQuantity();
                        }
                        if (processed[0] == cartList.size()) {
                            tvTotal.setText("Total: Rp " + total[0]);
                            this.currentTotalAmount = total[0];
                        }
                    })
                    .addOnFailureListener(err -> {
                        processed[0]++;
                        if (processed[0] == cartList.size()) {
                            tvTotal.setText("Total: Rp " + total[0]);
                            this.currentTotalAmount = total[0];
                        }
                    });
        }
    }

    private void performCheckout() {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
//        double total = 0;
//
//        for (CartItem ci : cartList) {
//            // hitung harga
//            // supaya cepat: ambil dari subtotal di UI atau fetch langsung
//            total += ci.getQuantity(); // nanti dikalikan harga di detail (biar simple di sini)
//        }

        // simpan ke Room
        TransactionOfflineDatabase dbRoom = TransactionOfflineDatabase.getInstance(this);
//        TransactionOffline trx = new TransactionOffline(uid, timestamp, total, new ArrayList<>(cartList));
//        dbRoom.transactionDao().insert(trx);

        // Gunakan variabel currentTotalAmount yang sudah berisi nilai benar
        TransactionOffline trx = new TransactionOffline(uid, timestamp, currentTotalAmount, new ArrayList<>(cartList));
        dbRoom.transactionDao().insert(trx);

        // ambil id terakhir (Room auto-generate ID, tapi untuk simple kita bisa getAll dan ambil paling depan)
        int newId = dbRoom.transactionDao().getAll().get(0).getId();

        // clear cart
        for (CartItem ci : cartList) {
            if (ci.getId() != null) {
                db.collection("carts").document(ci.getId()).delete();
            }
        }

        // buka TransactionDetailActivity
        Intent i = new Intent(this, TransactionDetailActivity.class);
        i.putExtra("transactionId", newId);
        startActivity(i);
        finish();

//        // hapus cart user di Firestore
//        for (CartItem ci : cartList) {
//            if (ci.getId() != null) {
//                db.collection("carts").document(ci.getId()).delete();
//            }
//        }
//
//        Toast.makeText(this, "Checkout success!", Toast.LENGTH_SHORT).show();
//        finish();
    }

}
