package com.example.foodapp_java.page.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodapp_java.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EditOutgoingStockActivity extends AppCompatActivity {

    private Spinner spinnerFood, spinnerSupplier, spinnerExpDate;
    private EditText etQty;
    private TextView tvCurrentStock;
    private Button btnSave;

    private FirebaseFirestore db;

    private String entryId;
    private DocumentSnapshot entrySnapshot;

    private String selectedFoodId, selectedSupplierId;
    private DocumentSnapshot selectedExpSnapshot;
    private List<DocumentSnapshot> expDateDocs = new ArrayList<>();

    private int originalQty;
    private String originalExpStockId;
    private String originalFoodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_outgoing_stock); // UI sama

        Toolbar toolbar = findViewById(R.id.toolbarAddOutgoingStock);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        db = FirebaseFirestore.getInstance();

        spinnerFood = findViewById(R.id.spinnerFood);
        spinnerSupplier = findViewById(R.id.spinnerSupplier);
        spinnerExpDate = findViewById(R.id.spinnerExpDate);
        etQty = findViewById(R.id.etQtyAddStock);
        tvCurrentStock = findViewById(R.id.tvCurrentStock);
        btnSave = findViewById(R.id.btnSaveOutgoingStock);

        // sembunyikan tanggal manual
        View btnPick = findViewById(R.id.btnPickExpDateAddStock);
        btnPick.setVisibility(View.GONE);

        entryId = getIntent().getStringExtra("entryId");
        if (entryId == null) {
            Toast.makeText(this, "Invalid entry", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEntryData();

        btnSave.setOnClickListener(v -> saveUpdatedEntry());
    }

    private void loadEntryData() {
        db.collection("outgoing_stocks").document(entryId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    entrySnapshot = doc;

                    originalQty = doc.getLong("qty").intValue();
                    originalExpStockId = doc.getString("expStockId");
                    originalFoodId = doc.getString("foodId");

                    etQty.setText(String.valueOf(originalQty));

                    loadFoods();
                    loadSuppliers();

                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadFoods() {
        db.collection("foods")
                .get()
                .addOnSuccessListener(q -> {

                    List<String> labels = new ArrayList<>();
                    List<String> ids = new ArrayList<>();

                    for (DocumentSnapshot d : q) {
                        labels.add(d.getString("name"));
                        ids.add(d.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item,
                            labels
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFood.setAdapter(adapter);

                    // set selection sesuai data awal
                    selectedFoodId = entrySnapshot.getString("foodId");
                    int index = ids.indexOf(selectedFoodId);
                    if (index >= 0) spinnerFood.setSelection(index);

                    spinnerFood.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                            selectedFoodId = ids.get(pos);
                            loadExpDates(selectedFoodId);
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                    });
                });
    }

    private void loadSuppliers() {
        db.collection("suppliers")
                .get()
                .addOnSuccessListener(q -> {

                    List<String> labels = new ArrayList<>();
                    List<String> ids = new ArrayList<>();

                    for (DocumentSnapshot d : q) {
                        labels.add(d.getString("name"));
                        ids.add(d.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item,
                            labels
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSupplier.setAdapter(adapter);

                    selectedSupplierId = entrySnapshot.getString("supplierId");
                    int index = ids.indexOf(selectedSupplierId);
                    if (index >= 0) spinnerSupplier.setSelection(index);

                    spinnerSupplier.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                            selectedSupplierId = ids.get(pos);
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                    });
                });
    }

    private void loadExpDates(String foodId) {
        expDateDocs.clear();

        db.collection("food_exp_date_stocks")
                .whereEqualTo("foodId", foodId)
                .get()
                .addOnSuccessListener(q -> {

                    List<String> labels = new ArrayList<>();

                    for (DocumentSnapshot d : q) {
                        Date exp = d.getDate("exp_date");
                        expDateDocs.add(d);
                        labels.add(android.text.format.DateFormat.format("yyyy-MM-dd", exp).toString());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item,
                            labels
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerExpDate.setAdapter(adapter);

                    // set selection ke expiry original
                    for (int i = 0; i < expDateDocs.size(); i++) {
                        if (expDateDocs.get(i).getId().equals(originalExpStockId)) {
                            spinnerExpDate.setSelection(i);
                            selectedExpSnapshot = expDateDocs.get(i);

                            long s = expDateDocs.get(i).getLong("stock_amount");
                            tvCurrentStock.setText("Sisa stok: " + s);
                            break;
                        }
                    }

                    spinnerExpDate.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                            selectedExpSnapshot = expDateDocs.get(pos);

                            Long st = selectedExpSnapshot.getLong("stock_amount");
                            tvCurrentStock.setText("Sisa stok: " + (st == null ? 0 : st));
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                    });
                });
    }

    private void saveUpdatedEntry() {

        String qtyStr = etQty.getText().toString().trim();
        if (TextUtils.isEmpty(qtyStr) || selectedFoodId == null || selectedExpSnapshot == null) {
            Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        int newQty;
        try {
            newQty = Integer.parseInt(qtyStr);
        } catch (Exception e) {
            Toast.makeText(this, "Qty tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newQty <= 0) {
            Toast.makeText(this, "Qty harus > 0", Toast.LENGTH_SHORT).show();
            return;
        }

        String newExpStockId = selectedExpSnapshot.getId();
        long newExpStock = selectedExpSnapshot.getLong("stock_amount");

        // cek stok cukup
        if (!newExpStockId.equals(originalExpStockId)) {

            // hapus keluar dari exp lama → balikin
            // potong ke exp baru
            if (newExpStock < newQty) {
                Toast.makeText(this, "Stok tidak cukup untuk expiry baru", Toast.LENGTH_SHORT).show();
                return;
            }

        } else {

            // expiry sama → beda qty
            long effectiveStock = newExpStock + originalQty; // dipulihkan dulu, baru dipotong lagi

            if (effectiveStock < newQty) {
                Toast.makeText(this, "Stok tidak cukup untuk update", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        WriteBatch batch = db.batch();

        DocumentReference entryRef = db.collection("outgoing_stocks").document(entryId);

        // 1. Kembalikan stok lama
        batch.update(
                db.collection("food_exp_date_stocks").document(originalExpStockId),
                "stock_amount",
                FieldValue.increment(originalQty)
        );

        // 2. Potong stok baru
        batch.update(
                db.collection("food_exp_date_stocks").document(newExpStockId),
                "stock_amount",
                FieldValue.increment(-newQty)
        );

        // 3. Update entry
        HashMap<String, Object> update = new HashMap<>();
        update.put("foodId", selectedFoodId);
        update.put("supplierId", selectedSupplierId);
        update.put("qty", newQty);
        update.put("expStockId", newExpStockId);
        update.put("exp_date", selectedExpSnapshot.getDate("exp_date"));
        update.put("timestamp", new Date());

        batch.update(entryRef, update);

        batch.commit()
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Berhasil diupdate", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
