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
import com.example.foodapp_java.adapter.EntryStockAdapter;
import com.example.foodapp_java.dataClass.EntryStock;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EntryStockActivity extends AppCompatActivity {

    private static final String TAG = "EntryStockActivity";
    private FirebaseFirestore db;
    private RecyclerView rv;
    private EntryStockAdapter adapter;
    private List<EntryStock> entries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_stock);

        Toolbar toolbar = findViewById(R.id.toolbarEntry);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // show back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        db = FirebaseFirestore.getInstance();

        rv = findViewById(R.id.rvEntries);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntryStockAdapter(this, entries, new EntryStockAdapter.OnActionListener() {
            @Override
            public void onEdit(EntryStock entry) {
                Intent i = new Intent(EntryStockActivity.this, EditEntryStockActivity.class);
                i.putExtra("entry", entry);
                i.putExtra("entryId", entry.getId()); // ✅ kirim juga ID dokumen
                startActivity(i);
            }

            @Override
            public void onDelete(EntryStock entry) {
                confirmAndDelete(entry);
            }
        });
        rv.setAdapter(adapter);

        findViewById(R.id.fabAddEntry).setOnClickListener(v -> {
            startActivity(new Intent(this, AddEntryStockActivity.class));
        });

        listenEntries();
    }

    private void listenEntries() {
        db.collection("entry_stocks")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Log.w(TAG, "listen failed", e);
                        return;
                    }
                    if (snap == null) return;
                    entries.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        EntryStock se = d.toObject(EntryStock.class);
                        if (se != null) {
                            se.setId(d.getId());
                            // ensure createdAt is set if stored as Timestamp
                            if (se.getCreatedAt() == null) {
                                Object o = d.get("createdAt");
                                if (o instanceof com.google.firebase.Timestamp) {
                                    se.setCreatedAt(((com.google.firebase.Timestamp) o).toDate());
                                } else {
                                    se.setCreatedAt(new Date());
                                }
                            }
                            // isi field tambahan dengan join ke Firestore
                            enrichEntry(se);
                            entries.add(se);
                        }
                    }
                    adapter.setList(entries);
                });
    }

//    private void enrichEntry(StockEntry se) {
//        // FOOD
//        db.collection("foods").document(se.getFoodId()).get()
//                .addOnSuccessListener(doc -> {
//                    if (doc.exists()) {
//                        se.setFoodName(doc.getString("name"));
//                        se.setImagePath(doc.getString("imagePath"));
//                        String catId = doc.getString("category_id");
//                        if (catId != null) {
//                            db.collection("categories").document(catId).get()
//                                    .addOnSuccessListener(cdoc -> {
//                                        if (cdoc.exists()) {
//                                            se.setCategoryName(cdoc.getString("name"));
//                                            adapter.notifyDataSetChanged();
//                                        }
//                                    });
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//
//        // SUPPLIER
//        db.collection("suppliers").document(se.getSupplierId()).get()
//                .addOnSuccessListener(doc -> {
//                    if (doc.exists()) {
//                        se.setSupplierName(doc.getString("name"));
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//
//        // USER (operator)
//        db.collection("users").document(se.getOperatorUid()).get()
//                .addOnSuccessListener(doc -> {
//                    if (doc.exists()) {
//                        se.setOperatorName(doc.getString("name"));
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//    }

    private void enrichEntry(EntryStock se) {
        // FOOD
        if (se.getFoodId() != null && !se.getFoodId().isEmpty()) {
            db.collection("foods").document(se.getFoodId()).get()
                    .addOnSuccessListener(doc -> {
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

    private void confirmAndDelete(EntryStock entry) {
        new AlertDialog.Builder(this)
                .setTitle("Delete entry")
                .setMessage("Delete this stock entry? This will decrease the related expiry stock.")
                .setPositiveButton("Delete", (dialog, which) -> deleteEntry(entry))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEntry(EntryStock entry) {
        if (entry == null) return;
        // batch: decrement food_exp_date_stocks.exp_stock_id by qty; then delete entry
        String expStockId = entry.getExpStockId();
        String entryId = entry.getId();
        if (expStockId == null || entryId == null) {
            Toast.makeText(this, "Invalid entry data", Toast.LENGTH_SHORT).show();
            return;
        }

        final com.google.firebase.firestore.DocumentReference expRef = db.collection("food_exp_date_stocks").document(expStockId);
        final com.google.firebase.firestore.DocumentReference entryRef = db.collection("entry_stocks").document(entryId);

        // read exp doc first to compute new stock
        expRef.get().addOnSuccessListener(doc -> {
            if (!doc.exists()) {
                // nothing to decrement, just delete entry
                entryRef.delete().addOnSuccessListener(aVoid -> Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(er -> Toast.makeText(this, "Delete failed: " + er.getMessage(), Toast.LENGTH_SHORT).show());
                return;
            }
            Long cur = doc.getLong("stock_amount");
            int curStock = cur == null ? 0 : cur.intValue();
            int newStock = curStock - entry.getQty();
            if (newStock < 0) newStock = 0;

            com.google.firebase.firestore.WriteBatch batch = db.batch();
            batch.update(expRef, "stock_amount", newStock);
            batch.delete(entryRef);
            batch.commit()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Entry deleted and stock adjusted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(er -> Toast.makeText(this, "Failed: " + er.getMessage(), Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(er -> Toast.makeText(this, "Failed read expiry: " + er.getMessage(), Toast.LENGTH_SHORT).show());
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
