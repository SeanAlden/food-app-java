//package com.example.foodapp_java.page;
//
//import android.app.DatePickerDialog;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.foodapp_java.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.WriteBatch;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class AddOutgoingStockActivity extends AppCompatActivity {
//
//    private Spinner spinnerFood, spinnerSupplier, spinnerExpDate;
//    private EditText etQty, btnPickDate;
//    private Button btnSave;
//    private FirebaseFirestore db;
//
//    private DocumentSnapshot selectedDate;
//    private Date selectedExpDate;
//    private ArrayAdapter<String> expDateAdapter;
//    private List<Date> expDates = new ArrayList<>();
//    private static final String NEW_DATE_OPTION = "Tambah tanggal baru...";
//
//    private String selectedFoodId, selectedSupplierId;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_outgoing_stock);
//
//        spinnerFood = findViewById(R.id.spinnerFood);
//        spinnerSupplier = findViewById(R.id.spinnerSupplier);
//        spinnerExpDate = findViewById(R.id.spinnerExpDate);
//        etQty = findViewById(R.id.etQtyAddStock);
//        btnPickDate = findViewById(R.id.btnPickExpDateAddStock);
//        btnSave = findViewById(R.id.btnSaveOutgoingStock);
//
//        db = FirebaseFirestore.getInstance();
//
//        // Adapter untuk ExpDate Spinner
//        expDateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
//        expDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerExpDate.setAdapter(expDateAdapter);
//
//        spinnerExpDate.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
//                if (NEW_DATE_OPTION.equals(expDateAdapter.getItem(pos))) {
//                    btnPickDate.setVisibility(View.VISIBLE);
//                } else {
//                    btnPickDate.setVisibility(View.GONE);
//                    if (pos < expDates.size()) {
//                        selectedExpDate = expDates.get(pos);
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(android.widget.AdapterView<?> parent) {
//            }
//        });
//
//        btnPickDate.setOnClickListener(v -> showDatePicker());
//
//        btnSave.setOnClickListener(v -> saveOutgoingStock());
//
//        loadFoods();
//        loadSuppliers();
//    }
//
//    private void showDatePicker() {
//        Calendar c = Calendar.getInstance();
//        DatePickerDialog dp = new DatePickerDialog(this, (DatePicker view, int year, int month, int dayOfMonth) -> {
//            Calendar sel = Calendar.getInstance();
//            sel.set(year, month, dayOfMonth, 0, 0, 0);
//            selectedExpDate = sel.getTime();
//            btnPickDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", selectedExpDate));
//        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        dp.show();
//    }
//
//    private void loadFoods() {
//        db.collection("foods").get().addOnSuccessListener(query -> {
//            List<String> labels = new ArrayList<>();
//            List<String> ids = new ArrayList<>();
//            for (DocumentSnapshot doc : query) {
//                labels.add(doc.getString("name"));
//                ids.add(doc.getId());
//            }
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerFood.setAdapter(adapter);
//
//            spinnerFood.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
//                    selectedFoodId = ids.get(pos);
//                    loadExpDates(selectedFoodId);
//                }
//
//                @Override
//                public void onNothingSelected(android.widget.AdapterView<?> parent) {
//                }
//            });
//        });
//    }
//
//    private void loadSuppliers() {
//        db.collection("suppliers").get().addOnSuccessListener(query -> {
//            List<String> labels = new ArrayList<>();
//            List<String> ids = new ArrayList<>();
//            for (DocumentSnapshot doc : query) {
//                labels.add(doc.getString("name"));
//                ids.add(doc.getId());
//            }
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerSupplier.setAdapter(adapter);
//
//            spinnerSupplier.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
//                    selectedSupplierId = ids.get(pos);
//                }
//
//                @Override
//                public void onNothingSelected(android.widget.AdapterView<?> parent) {
//                }
//            });
//        });
//    }
//
//    private void loadExpDates(String foodId) {
//        db.collection("food_exp_date_stocks").whereEqualTo("foodId", foodId).get().addOnSuccessListener(q -> {
//            expDates.clear();
//            List<String> labels = new ArrayList<>();
//            for (DocumentSnapshot d : q.getDocuments()) {
//                Date exp = d.getDate("exp_date");
//                if (exp != null) {
//                    expDates.add(exp);
//                    labels.add(android.text.format.DateFormat.format("yyyy-MM-dd", exp).toString());
//                }
//            }
//            labels.add(NEW_DATE_OPTION);
//            expDateAdapter.clear();
//            expDateAdapter.addAll(labels);
//            expDateAdapter.notifyDataSetChanged();
//            spinnerExpDate.setSelection(labels.size() - 1);
//        });
//    }
//
////    private void saveStock() {
////        String qtyStr = etQty.getText().toString().trim();
////        if (TextUtils.isEmpty(qtyStr) || selectedDate == null || selectedFoodId == null || selectedSupplierId == null) {
////            Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show();
////            return;
////        }
////
////        int qty = Integer.parseInt(qtyStr);
////        // TODO: Simpan ke Firestore sesuai struktur kamu
////        Toast.makeText(this, "Stock berhasil ditambahkan", Toast.LENGTH_SHORT).show();
////        finish();
////    }
//
//    private void saveOutgoingStock() {
//        String qtyStr = etQty.getText().toString().trim();
//
//        if (TextUtils.isEmpty(qtyStr) || selectedFoodId == null || selectedDate == null) {
//            Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        int qtyToRemove = Integer.parseInt(qtyStr);
//        if (qtyToRemove <= 0) {
//            Toast.makeText(this, "Jumlah harus lebih dari 0", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        long currentStock = selectedDate.getLong("stock_amount");
//        String expStockId = selectedDate.getId();
//        Date expDate = selectedDate.getDate("exp_date");
//
//        // =================================================================
//        // =========== LOGIKA VALIDASI SISA STOK DIBAWAH 0 =================
//        // =================================================================
//        if (currentStock < qtyToRemove) {
//            Toast.makeText(this, "Stok tidak mencukupi. Sisa stok: " + currentStock, Toast.LENGTH_LONG).show();
//            return; // Aksi dibatalkan
//        }
//        // =================================================================
//
//        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        WriteBatch batch = db.batch();
//
//        // 1. Kurangi stok di food_exp_date_stocks
//        batch.update(selectedDate.getReference(), "stock_amount", FieldValue.increment(-qtyToRemove));
//
//        // 2. Buat log di koleksi baru 'outgoing_stocks'
//        String outgoingId = db.collection("outgoing_stocks").document().getId();
//        Map<String, Object> outgoingData = new HashMap<>();
//        outgoingData.put("id", outgoingId);
//        outgoingData.put("foodId", selectedFoodId);
//        outgoingData.put("exp_date", expDate);
//        outgoingData.put("qty", qtyToRemove);
//        outgoingData.put("userId", userId);
//        outgoingData.put("timestamp", new Date());
//        outgoingData.put("expStockId", expStockId);
//
//        batch.set(db.collection("outgoing_stocks").document(outgoingId), outgoingData);
//
//        batch.commit().addOnSuccessListener(aVoid -> {
//            Toast.makeText(this, "Stok berhasil dikeluarkan", Toast.LENGTH_SHORT).show();
//            finish();
//        }).addOnFailureListener(e -> Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//    }
//}

package com.example.foodapp_java.page;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp_java.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddOutgoingStockActivity extends AppCompatActivity {

    private Spinner spinnerFood, spinnerSupplier, spinnerExpDate;
    private EditText etQty, btnPickDate;
    private Button btnSave;
    private TextView tvCurrentStock;
    private FirebaseFirestore db;

    // Simpan snapshot dokumen expiry agar mudah ambil id / stock_amount
    private DocumentSnapshot selectedExpSnapshot;
    private Date selectedExpDate;
    private ArrayAdapter<String> expDateAdapter;
    private List<DocumentSnapshot> expDateDocs = new ArrayList<>(); // <- DIUBAH: store snapshots
    private List<Date> expDates = new ArrayList<>();
    // tidak ada NEW_DATE_OPTION untuk outgoing

    private String selectedFoodId, selectedSupplierId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outgoing_stock);

        spinnerFood = findViewById(R.id.spinnerFood);
        spinnerSupplier = findViewById(R.id.spinnerSupplier);
        spinnerExpDate = findViewById(R.id.spinnerExpDate);
        etQty = findViewById(R.id.etQtyAddStock);
        btnPickDate = findViewById(R.id.btnPickExpDateAddStock); // tetap ada di layout; akan disembunyikan
        btnSave = findViewById(R.id.btnSaveOutgoingStock);
        tvCurrentStock = findViewById(R.id.tvCurrentStock);

        db = FirebaseFirestore.getInstance();

        // hide pick-date EditText because outgoing should only pick existing expiry
        if (btnPickDate != null) btnPickDate.setVisibility(View.GONE);

        // Adapter untuk ExpDate Spinner (HANYA tanggal yang sudah ada)
        expDateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        expDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpDate.setAdapter(expDateAdapter);

        spinnerExpDate.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                // set selected snapshot & date sesuai index snapshot list
                if (pos >= 0 && pos < expDateDocs.size()) {
                    selectedExpSnapshot = expDateDocs.get(pos);
                    selectedExpDate = selectedExpSnapshot.getDate("exp_date");

                    // tampilkan stok preview
                    Long stock = selectedExpSnapshot.getLong("stock_amount");
                    long currentStock = stock == null ? 0L : stock;
                    tvCurrentStock.setText("Sisa stok: " + currentStock);
                } else {
                    selectedExpSnapshot = null;
                    selectedExpDate = null;
                    tvCurrentStock.setText("Sisa stok: -");
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedExpSnapshot = null;
                selectedExpDate = null;
                tvCurrentStock.setText("Sisa Stok: -");
            }
        });

        btnSave.setOnClickListener(v -> saveOutgoingStock());

        loadFoods();
        loadSuppliers();
    }

    private void showDatePicker() {
        // not used for outgoing but kept for completeness (hidden)
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (DatePicker view, int year, int month, int dayOfMonth) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(year, month, dayOfMonth, 0, 0, 0);
            selectedExpDate = sel.getTime();
            if (btnPickDate != null)
                btnPickDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", selectedExpDate));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }

    private void loadFoods() {
        db.collection("foods").get().addOnSuccessListener(query -> {
            List<String> labels = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            for (DocumentSnapshot doc : query) {
                labels.add(doc.getString("name"));
                ids.add(doc.getId());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFood.setAdapter(adapter);

            spinnerFood.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                    selectedFoodId = ids.get(pos);
                    // reload expiry list for selected food
                    loadExpDates(selectedFoodId);
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    selectedFoodId = null;
                    expDateDocs.clear();
                    expDates.clear();
                    expDateAdapter.clear();
                    expDateAdapter.notifyDataSetChanged();
                    tvCurrentStock.setText("Sisa stok: -");
                }
            });
        });
    }

    private void loadSuppliers() {
        db.collection("suppliers").get().addOnSuccessListener(query -> {
            List<String> labels = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            for (DocumentSnapshot doc : query) {
                labels.add(doc.getString("name"));
                ids.add(doc.getId());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSupplier.setAdapter(adapter);

            spinnerSupplier.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                    selectedSupplierId = ids.get(pos);
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                    selectedSupplierId = null;
                }
            });
        });
    }

    private void loadExpDates(String foodId) {
        // clear previous
        expDateDocs.clear();
        expDates.clear();

        db.collection("food_exp_date_stocks")
                .whereEqualTo("foodId", foodId)
                .get()
                .addOnSuccessListener(q -> {
                    List<String> labels = new ArrayList<>();
                    for (DocumentSnapshot d : q.getDocuments()) {
                        Date exp = d.getDate("exp_date");
                        if (exp != null) {
                            expDateDocs.add(d);                // store snapshot
                            expDates.add(exp);
                            labels.add(android.text.format.DateFormat.format("yyyy-MM-dd", exp).toString());
                        }
                    }

                    // NOTE: DO NOT add "Tambah tanggal baru..." for outgoing stocks
                    expDateAdapter.clear();
                    expDateAdapter.addAll(labels);

                    expDateAdapter.notifyDataSetChanged();

                    // select first if available
                    if (!labels.isEmpty()) {
                        spinnerExpDate.setSelection(0);
                        selectedExpSnapshot = expDateDocs.get(0);
                        selectedExpDate = selectedExpSnapshot.getDate("exp_date");
                    } else {
                        selectedExpSnapshot = null;
                        selectedExpDate = null;
                        tvCurrentStock.setText("Sisa stok: -");
                    }
                })
                .addOnFailureListener(err -> {
                    expDateDocs.clear();
                    expDates.clear();
                    expDateAdapter.clear();
                    expDateAdapter.notifyDataSetChanged();
                });
    }

    private void saveOutgoingStock() {
        String qtyStr = etQty.getText().toString().trim();

        // VALIDATION: qty, food selected, and an existing expiry selected
        if (TextUtils.isEmpty(qtyStr) || selectedFoodId == null || selectedExpSnapshot == null) {
            Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        int qtyToRemove;
        try {
            qtyToRemove = Integer.parseInt(qtyStr);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Jumlah tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        if (qtyToRemove <= 0) {
            Toast.makeText(this, "Jumlah harus lebih dari 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil stock sekarang dari snapshot
        Long curLong = selectedExpSnapshot.getLong("stock_amount");
        long currentStock = curLong == null ? 0L : curLong.longValue();
        String expStockId = selectedExpSnapshot.getId();
        Date expDate = selectedExpSnapshot.getDate("exp_date");

        // Validasi stok cukup
        if (currentStock < qtyToRemove) {
            Toast.makeText(this, "Stok tidak mencukupi. Sisa stok: " + currentStock, Toast.LENGTH_LONG).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        WriteBatch batch = db.batch();

        // 1) kurangi stok pada expiry doc
        batch.update(selectedExpSnapshot.getReference(), "stock_amount", FieldValue.increment(-qtyToRemove));

        // 2) simpan log outgoing_stocks
        String outgoingId = db.collection("outgoing_stocks").document().getId();
        Map<String, Object> outgoingData = new HashMap<>();
        outgoingData.put("id", outgoingId);
        outgoingData.put("foodId", selectedFoodId);
        outgoingData.put("exp_date", expDate);
        outgoingData.put("qty", qtyToRemove);
        outgoingData.put("userId", userId);
        outgoingData.put("timestamp", new Date());
        outgoingData.put("expStockId", expStockId);
        outgoingData.put("supplierId", selectedSupplierId); // optional
        batch.set(db.collection("outgoing_stocks").document(outgoingId), outgoingData);

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Stok berhasil dikeluarkan", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
