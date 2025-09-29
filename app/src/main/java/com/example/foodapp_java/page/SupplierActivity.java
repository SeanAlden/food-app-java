// path: app/src/main/java/com/example/foodapp_java/page/SupplierActivity.java
package com.example.foodapp_java.page;

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
import com.example.foodapp_java.adapter.AdapterSupplier;
import com.example.foodapp_java.dataClass.Supplier;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class SupplierActivity extends AppCompatActivity {

    private RecyclerView rvSuppliers;
    private AdapterSupplier adapter;
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

        adapter = new AdapterSupplier(this, list, supplier -> {
            // open detail
            Intent i = new Intent(SupplierActivity.this, DetailSupplierActivity.class);
            i.putExtra("supplier", supplier);
            startActivityForResult(i, 2000);
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

    private void startListener() {
        supplierListener = db.collection("suppliers")
                .orderBy("name")
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Listen failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snap == null) return;
                    list.clear();
                    for (DocumentSnapshot d : snap.getDocuments()) {
                        Supplier s = d.toObject(Supplier.class);
                        if (s != null) {
                            s.setId(d.getId());
                            list.add(s);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}
