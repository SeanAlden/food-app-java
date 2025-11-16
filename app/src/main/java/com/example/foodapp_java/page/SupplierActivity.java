// path: app/src/main/java/com/example/foodapp_java/page/SupplierActivity.java
package com.example.foodapp_java.page;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.SupplierAdapter;
import com.example.foodapp_java.dataClass.Supplier;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SupplierActivity extends AppCompatActivity {

    private RecyclerView rvSuppliers;
    private SupplierAdapter adapter;
    private List<Supplier> list;
    private FirebaseFirestore db;
    private ListenerRegistration supplierListener;
    private ActivityResultLauncher<Intent> addLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarSupplier);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        rvSuppliers = findViewById(R.id.rvSuppliers);
        rvSuppliers.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

//        adapter = new SupplierAdapter(this, list, supplier -> {
//            // open detail
//            Intent i = new Intent(SupplierActivity.this, DetailSupplierActivity.class);
//            i.putExtra("supplier", supplier);
//            startActivityForResult(i, 2000);
//        });

        adapter = new SupplierAdapter(this, list, new SupplierAdapter.SupplierListener() {
            @Override
            public void onEdit(Supplier supplier) {
                Intent i = new Intent(SupplierActivity.this, EditSupplierActivity.class);
                i.putExtra("supplier", supplier);
                addLauncher.launch(i);
            }

            @Override
            public void onDelete(Supplier supplier) {
                new AlertDialog.Builder(SupplierActivity.this)
                        .setTitle("Delete supplier")
                        .setMessage("Are you sure to delete this supplier?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            db.collection("suppliers").document(supplier.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // delete local image
                                        String img = supplier.getImage();
                                        if (img != null && !img.isEmpty()) {
                                            File f = new File(img);
                                            if (f.exists()) f.delete();
                                        }
                                        Toast.makeText(SupplierActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(SupplierActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            // open detail

            @Override
            public void onClick(Supplier supplier) {
                Intent i = new Intent(SupplierActivity.this, DetailSupplierActivity.class);
                i.putExtra("supplier", supplier);
                startActivityForResult(i, 2000);
            }
        });

        rvSuppliers.setAdapter(adapter);

        findViewById(R.id.fabAddSupplier).setOnClickListener(v -> {
            Intent i = new Intent(this, AddSupplierActivity.class);
            addLauncher.launch(i);
        });

        addLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                // snapshot listener will auto update
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        startListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (supplierListener != null) {
            supplierListener.remove();
            supplierListener = null;
        }
    }

//    private void startListener() {
//        supplierListener = db.collection("suppliers")
//                .orderBy("name")
//                .addSnapshotListener((snap, e) -> {
//                    if (e != null) {
//                        Toast.makeText(this, "Listen failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    if (snap == null) return;
//                    list.clear();
//                    for (DocumentSnapshot d : snap.getDocuments()) {
//                        Supplier s = d.toObject(Supplier.class);
//                        if (s != null) {
//                            s.setId(d.getId());
//                            list.add(s);
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                });
//    }

    private void startListener() {
        supplierListener = db.collection("suppliers")
                .orderBy("name")
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Listen failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snap == null) return;

                    List<Supplier> suppliers = new ArrayList<>();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        Supplier s = d.toObject(Supplier.class);
                        if (s != null) {
                            s.setId(d.getId());
                            suppliers.add(s);
                        }
                    }

                    // === PRE CHECK hubungan sebelum tampil ===
                    preCheckSupplierRelations(suppliers);
                });
    }

    private void preCheckSupplierRelations(List<Supplier> suppliers) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("entry_stocks").get().addOnSuccessListener(entrySnap -> {
            db.collection("outgoing_stocks").get().addOnSuccessListener(outSnap -> {

                for (Supplier s : suppliers) {
                    boolean used = false;

                    // Cek EntryStock
                    for (DocumentSnapshot d : entrySnap) {
                        if (s.getId().equals(d.getString("supplierId"))) {
                            used = true;
                            break;
                        }
                    }

                    // Cek OutgoingStock
                    if (!used) {
                        for (DocumentSnapshot d : outSnap) {
                            if (s.getId().equals(d.getString("supplierId"))) {
                                used = true;
                                break;
                            }
                        }
                    }

                    s.setCanDelete(!used);   // <- Flag disable delete
                }

                // Setelah selesai cek → update adapter
                list.clear();
                list.addAll(suppliers);
                adapter.notifyDataSetChanged();

            });
        });
    }
}
