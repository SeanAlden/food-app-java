package com.example.foodapp_java.page.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.OutgoingStockAdapter; // Diubah
import com.example.foodapp_java.dataClass.OutgoingStock; // Diubah
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class OutgoingStockActivity extends AppCompatActivity {

    private static final String TAG = "OutgoingStockActivity";
    private FirebaseFirestore db;
    private RecyclerView rv;
    private OutgoingStockAdapter adapter;
    private List<OutgoingStock> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_stock);

        Toolbar toolbar = findViewById(R.id.toolbarOutgoing);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();

        rv = findViewById(R.id.rvOutgoing);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OutgoingStockAdapter(this, entries, new OutgoingStockAdapter.OnActionListener() {
            @Override
            public void onEdit(OutgoingStock entry) {
                 Intent i = new Intent(OutgoingStockActivity.this, EditOutgoingStockActivity.class);
                 i.putExtra("entry", entry);
                 i.putExtra("entryId", entry.getId());
                 startActivity(i);
//                Toast.makeText(OutgoingStockActivity.this, "Fitur Edit belum diimplementasikan", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(OutgoingStock entry) {
                confirmAndDelete(entry);
            }
        });
        rv.setAdapter(adapter);

        findViewById(R.id.fabAddOutgoing).setOnClickListener(v -> {
            startActivity(new Intent(this, AddOutgoingStockActivity.class));
        });

        listenEntries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenEntries(); // Refresh data saat kembali ke activity
    }

    private void listenEntries() {
        db.collection("outgoing_stocks") // Koleksi baru
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen failed", e);
                        return;
                    }
                    if (snap == null) return;
                    entries.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        OutgoingStock se = d.toObject(OutgoingStock.class);
                        if (se != null) {
                            se.setId(d.getId());
                            enrichEntry(se);
                            entries.add(se);
                        }
                    }
                    adapter.setList(entries);
                });
    }

    private void enrichEntry(OutgoingStock se) {
        // Logika ini sama, hanya untuk melengkapi data nama, kategori, dll.
        if (se.getFoodId() != null) {
            db.collection("foods").document(se.getFoodId()).get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    se.setFoodName(doc.getString("name"));
                    se.setImagePath(doc.getString("imagePath"));
                    String catId = doc.getString("category_id");
                    if (catId != null && !catId.isEmpty()) {
                        db.collection("categories").document(catId).get()
                                .addOnSuccessListener(cdoc -> {
                                    if (cdoc.exists()) {
                                        se.setCategoryName(cdoc.getString("name"));
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
        // SUPPLIER
        if (se.getSupplierId() != null && !se.getSupplierId().isEmpty()) {
            db.collection("suppliers").document(se.getSupplierId()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            se.setSupplierName(doc.getString("name"));
                            adapter.notifyDataSetChanged();
                        }
                    });
        }

        // USER (operator)
        if (se.getUserId() != null && !se.getUserId().isEmpty()) {
            db.collection("users").document(se.getUserId()).get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            se.setOperatorName(doc.getString("name"));
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    private void confirmAndDelete(OutgoingStock entry) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Outgoing Entry")
                .setMessage("Yakin ingin menghapus? Ini akan MENGEMBALIKAN jumlah stok yang dikeluarkan.")
                .setPositiveButton("Delete", (dialog, which) -> deleteEntry(entry))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEntry(OutgoingStock entry) {
        // Logika kebalikan: jika log barang keluar dihapus, stoknya harus dikembalikan
        String expStockId = entry.getExpStockId();
        String entryId = entry.getId();
        if (expStockId == null || entryId == null) {
            Toast.makeText(this, "Invalid entry data", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference expRef = db.collection("food_exp_date_stocks").document(expStockId);
        DocumentReference entryRef = db.collection("outgoing_stocks").document(entryId);

        WriteBatch batch = db.batch();

        // 1. Tambahkan kembali stok (increment)
        batch.update(expRef, "stock_amount", FieldValue.increment(entry.getQty()));

        // 2. Hapus log barang keluar
        batch.delete(entryRef);

        batch.commit()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Log dihapus, stok dikembalikan", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(er -> Toast.makeText(this, "Gagal: " + er.getMessage(), Toast.LENGTH_SHORT).show());
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}