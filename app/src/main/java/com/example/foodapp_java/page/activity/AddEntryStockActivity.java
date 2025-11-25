//package com.example.foodapp_java.page;
//
//import android.app.DatePickerDialog;
//import android.app.ProgressDialog;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.dataClass.Food;
//import com.example.foodapp_java.dataClass.Supplier;
//import com.google.firebase.Timestamp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.firestore.WriteBatch;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class AddStockActivity extends AppCompatActivity {
//    private static final String TAG = "AddStockActivity";
//    private Spinner spinnerFood, spinnerSupplier;
//    private EditText etQty, btnPickDate;
//    private Button btnSave;
//    private FirebaseFirestore db;
//    private List<Food> foods = new ArrayList<>();
//    private List<Supplier> suppliers = new ArrayList<>();
//    private ArrayAdapter<String> foodAdapter;
//    private ArrayAdapter<String> supplierAdapter;
//    private Date selectedDate = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_stock);
//
//        db = FirebaseFirestore.getInstance();
//
//        spinnerFood = findViewById(R.id.spinnerFood);
//        spinnerSupplier = findViewById(R.id.spinnerSupplier);
//        etQty = findViewById(R.id.etQtyAddStock);
//        btnPickDate = findViewById(R.id.btnPickExpDateAddStock);
//        btnSave = findViewById(R.id.btnSaveAddStock);
//
//        foodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
//        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerFood.setAdapter(foodAdapter);
//
//        supplierAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
//        supplierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerSupplier.setAdapter(supplierAdapter);
//
//        btnPickDate.setOnClickListener(v -> {
//            Calendar c = Calendar.getInstance();
//            DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
//                Calendar sel = Calendar.getInstance();
//                sel.set(year, month, dayOfMonth, 0, 0, 0);
//                selectedDate = sel.getTime();
//                btnPickDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", selectedDate));
//            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//            dp.show();
//        });
//
//        btnSave.setOnClickListener(v -> saveEntry());
//
//        loadFoods();
//        loadSuppliers();
//    }
//
//    private void loadFoods() {
//        db.collection("foods").whereEqualTo("status", "active")
//                .get().addOnSuccessListener(QuerySnapshot::getDocuments)
//                .addOnSuccessListener(docs -> {
//                    // this block won't be used because of method chain; using safe approach below
//                }).addOnFailureListener(e -> Log.w(TAG, "load foods failed", e));
//
//        db.collection("foods").whereEqualTo("status", "active")
//                .get()
//                .addOnSuccessListener(q -> {
//                    foods.clear();
//                    List<String> labels = new ArrayList<>();
//                    for (DocumentReference ref : q.getDocuments().stream().map(d -> d.getReference()).toArray(DocumentReference[]::new)) {
//                        // unreachable trick — better use normal loop below
//                    }
//                }).addOnFailureListener(e -> Log.w(TAG, "load foods failed", e));
//
//        // simpler correct implementation:
//        db.collection("foods").whereEqualTo("status", "active")
//                .get().addOnSuccessListener(q -> {
//                    foods.clear();
//                    List<String> labels = new ArrayList<>();
//                    for (DocumentSnapshot d : q.getDocuments()) {
//                        Food f = d.toObject(Food.class);
//                        if (f != null) {
//                            f.setId(d.getId());
//                            foods.add(f);
//                            labels.add(f.getName());
//                        }
//                    }
//                    foodAdapter.clear();
//                    foodAdapter.addAll(labels);
//                    foodAdapter.notifyDataSetChanged();
//                }).addOnFailureListener(e -> Log.w(TAG, "load foods failed", e));
//    }
//
//    private void loadSuppliers() {
//        db.collection("suppliers").get()
//                .addOnSuccessListener(q -> {
//                    suppliers.clear();
//                    List<String> labels = new ArrayList<>();
//                    for (com.google.firebase.firestore.DocumentSnapshot d : q.getDocuments()) {
//                        Supplier s = d.toObject(Supplier.class);
//                        if (s != null) {
//                            s.setId(d.getId());
//                            suppliers.add(s);
//                            labels.add(s.getName());
//                        }
//                    }
//                    supplierAdapter.clear();
//                    supplierAdapter.addAll(labels);
//                    supplierAdapter.notifyDataSetChanged();
//                }).addOnFailureListener(e -> Log.w(TAG, "load supp failed", e));
//    }
//
//    private void saveEntry() {
//        int posFood = spinnerFood.getSelectedItemPosition();
//        int posSup = spinnerSupplier.getSelectedItemPosition();
//        if (posFood < 0 || posFood >= foods.size()) { Toast.makeText(this, "Select food", Toast.LENGTH_SHORT).show(); return; }
//        if (posSup < 0 || posSup >= suppliers.size()) { Toast.makeText(this, "Select supplier", Toast.LENGTH_SHORT).show(); return; }
//        if (selectedDate == null) { Toast.makeText(this, "Pick expiry date", Toast.LENGTH_SHORT).show(); return; }
//        String qtyStr = etQty.getText().toString().trim();
//        if (qtyStr.isEmpty()) { etQty.setError("qty"); return; }
//        int qty = Integer.parseInt(qtyStr);
//        if (qty <= 0) { etQty.setError("Invalid qty"); return; }
//
//        Food f = foods.get(posFood);
//        Supplier s = suppliers.get(posSup);
//        String operatorUid = FirebaseAuth.getInstance().getCurrentUser() == null ? "unknown" : FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Saving...");
//        pd.setCancelable(false);
//        pd.show();
//
//        // Step 1: check if expiry stock doc exists for this foodId + expDate
//        db.collection("food_exp_date_stocks")
//                .whereEqualTo("foodId", f.getId())
//                .whereEqualTo("exp_date", selectedDate)
//                .get()
//                .addOnSuccessListener(qsnap -> {
//                    try {
//                        WriteBatch batch = db.batch();
//                        String expStockId;
//                        if (!qsnap.isEmpty()) {
//                            // use first doc
//                            com.google.firebase.firestore.DocumentSnapshot doc = qsnap.getDocuments().get(0);
//                            expStockId = doc.getId();
//                            Long cur = doc.getLong("stock_amount");
//                            int curStock = cur == null ? 0 : cur.intValue();
//                            int newStock = curStock + qty;
//                            batch.update(db.collection("food_exp_date_stocks").document(expStockId), "stock_amount", newStock);
//                        } else {
//                            // create new exp stock doc
//                            expStockId = db.collection("food_exp_date_stocks").document().getId();
//                            com.google.firebase.firestore.DocumentReference expRef = db.collection("food_exp_date_stocks").document(expStockId);
//                            Map<String, Object> expMap = new HashMap<>();
//                            expMap.put("id", expStockId);
//                            expMap.put("foodId", f.getId());
//                            expMap.put("stock_amount", qty);
//                            expMap.put("exp_date", selectedDate);
//                            batch.set(expRef, expMap);
//                        }
//
//                        // create stock_entry doc
//                        String entryId = db.collection("stock_entries").document().getId();
//                        com.google.firebase.firestore.DocumentReference entryRef = db.collection("stock_entries").document(entryId);
//                        Map<String, Object> entryMap = new HashMap<>();
//                        entryMap.put("id", entryId);
//                        entryMap.put("foodId", f.getId());
//                        entryMap.put("foodName", f.getName());
//                        entryMap.put("expDate", selectedDate);
//                        entryMap.put("qty", qty);
//                        entryMap.put("supplierId", s.getId());
//                        entryMap.put("supplierName", s.getName());
//                        entryMap.put("operatorUid", operatorUid);
//                        entryMap.put("operatorName", ""); // you may fetch name if needed
//                        entryMap.put("expStockId", (qsnap.isEmpty() ? db.collection("food_exp_date_stocks").document().getId() : qsnap.getDocuments().get(0).getId()));
//                        entryMap.put("createdAt", Timestamp.now());
//                        batch.set(entryRef, entryMap);
//
//                        // commit
//                        batch.commit().addOnSuccessListener(aVoid -> {
//                            pd.dismiss();
//                            Toast.makeText(this, "Entry saved", Toast.LENGTH_SHORT).show();
//                            setResult(RESULT_OK);
//                            finish();
//                        }).addOnFailureListener(err -> {
//                            pd.dismiss();
//                            Toast.makeText(this, "Failed: " + err.getMessage(), Toast.LENGTH_SHORT).show();
//                        });
//                    } catch (Exception ex) {
//                        pd.dismiss();
//                        Log.e(TAG, "error", ex);
//                        Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(e -> {
//                    pd.dismiss();
//                    Toast.makeText(this, "Lookup failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//}

package com.example.foodapp_java.page.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodapp_java.R;
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

public class AddEntryStockActivity extends AppCompatActivity {

    private Spinner spinnerFood, spinnerSupplier, spinnerExpDate;
    private EditText etQty, btnPickDate;
    private Button btnSave;
    private FirebaseFirestore db;

    private Date selectedDate;
    private ArrayAdapter<String> expDateAdapter;
    private List<Date> expDates = new ArrayList<>();
    private static final String NEW_DATE_OPTION = "Tambah tanggal baru...";

    private String selectedFoodId, selectedSupplierId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry_stock);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarAddEntryStock);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spinnerFood = findViewById(R.id.spinnerFood);
        spinnerSupplier = findViewById(R.id.spinnerSupplier);
        spinnerExpDate = findViewById(R.id.spinnerExpDate);
        etQty = findViewById(R.id.etQtyAddStock);
        btnPickDate = findViewById(R.id.btnPickExpDateAddStock);
        btnSave = findViewById(R.id.btnSaveAddStock);

        db = FirebaseFirestore.getInstance();

        // Adapter untuk ExpDate Spinner
        expDateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        expDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpDate.setAdapter(expDateAdapter);

        spinnerExpDate.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                if (NEW_DATE_OPTION.equals(expDateAdapter.getItem(pos))) {
                    btnPickDate.setVisibility(View.VISIBLE);
                } else {
                    btnPickDate.setVisibility(View.GONE);
                    if (pos < expDates.size()) {
                        selectedDate = expDates.get(pos);
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        btnPickDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> saveStock());

        loadFoods();
        loadSuppliers();
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (DatePicker view, int year, int month, int dayOfMonth) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(year, month, dayOfMonth, 0, 0, 0);
            selectedDate = sel.getTime();
            btnPickDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", selectedDate));
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
                    loadExpDates(selectedFoodId);
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
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
                }
            });
        });
    }

    private void loadExpDates(String foodId) {
        db.collection("food_exp_date_stocks").whereEqualTo("foodId", foodId).get().addOnSuccessListener(q -> {
            expDates.clear();
            List<String> labels = new ArrayList<>();
            for (DocumentSnapshot d : q.getDocuments()) {
                Date exp = d.getDate("exp_date");
                if (exp != null) {
                    expDates.add(exp);
                    labels.add(android.text.format.DateFormat.format("yyyy-MM-dd", exp).toString());
                }
            }
            labels.add(NEW_DATE_OPTION);
            expDateAdapter.clear();
            expDateAdapter.addAll(labels);
            expDateAdapter.notifyDataSetChanged();
            spinnerExpDate.setSelection(labels.size() - 1);
        });
    }

//    private void saveStock() {
//        String qtyStr = etQty.getText().toString().trim();
//        if (TextUtils.isEmpty(qtyStr) || selectedDate == null || selectedFoodId == null || selectedSupplierId == null) {
//            Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        int qty = Integer.parseInt(qtyStr);
//        // TODO: Simpan ke Firestore sesuai struktur kamu
//        Toast.makeText(this, "Stock berhasil ditambahkan", Toast.LENGTH_SHORT).show();
//        finish();
//    }

    private void saveStock() {
        String qtyStr = etQty.getText().toString().trim();
        if (TextUtils.isEmpty(qtyStr) || selectedDate == null || selectedFoodId == null || selectedSupplierId == null) {
            Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty = Integer.parseInt(qtyStr);

        // Ambil UID user yang sedang login
        String userId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("food_exp_date_stocks").whereEqualTo("foodId", selectedFoodId).whereEqualTo("exp_date", selectedDate).get().addOnSuccessListener(query -> {
            if (query.isEmpty()) {
                // 🔹 Tanggal kadaluarsa baru → buat dokumen stok baru
                String stockId = db.collection("food_exp_date_stocks").document().getId();
                java.util.Map<String, Object> stockData = new java.util.HashMap<>();
                stockData.put("id", stockId);
                stockData.put("foodId", selectedFoodId);
                stockData.put("stock_amount", qty);
                stockData.put("exp_date", selectedDate);

                db.collection("food_exp_date_stocks").document(stockId).set(stockData);
            } else {
                // 🔹 Sudah ada stok untuk foodId + expDate → update jumlah stok
                DocumentSnapshot doc = query.getDocuments().get(0);
                String stockId = doc.getId();
                int oldStock = doc.getLong("stock_amount").intValue();
                int newStock = oldStock + qty;

                db.collection("food_exp_date_stocks").document(stockId).update("stock_amount", newStock);
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            WriteBatch batch = db.batch(); // inisialisasi batch

            String expStockId;
            if (query.isEmpty()) {
                String stockId = db.collection("food_exp_date_stocks").document().getId();
                expStockId = stockId;
                // isi stock baru
                Map<String, Object> stockData = new HashMap<>();
                stockData.put("foodId", selectedFoodId);
                stockData.put("exp_date", selectedDate);
                stockData.put("qty", qty);
                batch.set(db.collection("food_exp_date_stocks").document(stockId), stockData);
            } else {
                DocumentSnapshot doc = query.getDocuments().get(0);
                expStockId = doc.getId();
                batch.update(doc.getReference(), "qty", FieldValue.increment(qty));
            }

// simpan ke entry_stocks
            String entryId = db.collection("entry_stocks").document().getId();
            Map<String, Object> entryData = new HashMap<>();
            entryData.put("id", entryId);
            entryData.put("foodId", selectedFoodId);
            entryData.put("exp_date", selectedDate);
            entryData.put("qty", qty);
            entryData.put("supplierId", selectedSupplierId);
            entryData.put("userId", userId);
            entryData.put("timestamp", new Date());
            entryData.put("expStockId", expStockId); // ✅ penting

            // 🔹 Simpan ke riwayat entry_stocks
//                    String entryId = db.collection("entry_stocks").document().getId();
//                    java.util.Map<String, Object> entryData = new java.util.HashMap<>();
//                    entryData.put("id", entryId);
//                    entryData.put("foodId", selectedFoodId);
//                    entryData.put("exp_date", selectedDate);
//                    entryData.put("qty", qty);
//                    entryData.put("supplierId", selectedSupplierId);
//                    entryData.put("userId", userId);
//                    entryData.put("timestamp", new java.util.Date());

            db.collection("entry_stocks").document(entryId).set(entryData).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Stock berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(this, "Gagal menambahkan stok", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
