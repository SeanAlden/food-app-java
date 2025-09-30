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
//import com.example.foodapp_java.dataClass.StockEntry;
//import com.example.foodapp_java.dataClass.Food;
//import com.example.foodapp_java.dataClass.Supplier;
//import com.google.firebase.Timestamp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.DocumentReference;
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
//public class EditStockActivity extends AppCompatActivity {
//    private static final String TAG = "EditStockActivity";
//    private Spinner spinnerFood, spinnerSupplier;
//    private EditText etQty, btnPickDate;
//    private Button btnSave;
//    private FirebaseFirestore db;
//    private List<Food> foods = new ArrayList<>();
//    private List<Supplier> suppliers = new ArrayList<>();
//    private ArrayAdapter<String> foodAdapter, supplierAdapter;
//    private Date selectedDate;
//    private StockEntry entry;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_stock);
//
//        db = FirebaseFirestore.getInstance();
//
//        spinnerFood = findViewById(R.id.spinnerFoodEdit);
//        spinnerSupplier = findViewById(R.id.spinnerSupplierEdit);
//        etQty = findViewById(R.id.etQtyEditStock);
//        btnPickDate = findViewById(R.id.btnPickExpDateEditStock);
//        btnSave = findViewById(R.id.btnSaveEditStock);
//
//        foodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
//        foodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerFood.setAdapter(foodAdapter);
//
//        supplierAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
//        supplierAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerSupplier.setAdapter(supplierAdapter);
//
//        entry = getIntent().getParcelableExtra("entry");
//        if (entry == null) {
//            Toast.makeText(this, "No entry provided", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
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
//        btnSave.setOnClickListener(v -> onSaveEdit());
//
//        loadFoodsSuppliersAndFill();
//    }
//
//    private void loadFoodsSuppliersAndFill() {
//        db.collection("foods").whereEqualTo("status", "active")
//                .get().addOnSuccessListener(q -> {
//                    foods.clear();
//                    List<String> labels = new ArrayList<>();
//                    int selIndexFood = 0;
//                    for (com.google.firebase.firestore.DocumentSnapshot d : q.getDocuments()) {
//                        Food f = d.toObject(Food.class);
//                        if (f != null) {
//                            f.setId(d.getId());
//                            foods.add(f);
//                            labels.add(f.getName());
//                            if (f.getId().equals(entry.getFoodId())) selIndexFood = labels.size() - 1;
//                        }
//                    }
//                    foodAdapter.clear();
//                    foodAdapter.addAll(labels);
//                    foodAdapter.notifyDataSetChanged();
//                    spinnerFood.setSelection(selIndexFood);
//                }).addOnFailureListener(e -> {});
//
//        db.collection("suppliers").get().addOnSuccessListener(q -> {
//            suppliers.clear();
//            List<String> labels = new ArrayList<>();
//            int selIndex = 0;
//            for (com.google.firebase.firestore.DocumentSnapshot d : q.getDocuments()) {
//                Supplier s = d.toObject(Supplier.class);
//                if (s != null) {
//                    s.setId(d.getId());
//                    suppliers.add(s);
//                    labels.add(s.getName());
//                    if (s.getId().equals(entry.getSupplierId())) selIndex = labels.size() - 1;
//                }
//            }
//            supplierAdapter.clear();
//            supplierAdapter.addAll(labels);
//            supplierAdapter.notifyDataSetChanged();
//            spinnerSupplier.setSelection(selIndex);
//        }).addOnFailureListener(e -> {});
//
//        // fill qty & date
//        etQty.setText(String.valueOf(entry.getQty()));
//        if (entry.getExpDate() != null) {
//            selectedDate = entry.getExpDate();
//            btnPickDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", selectedDate));
//        }
//    }
//
//    private void onSaveEdit() {
//        int posFood = spinnerFood.getSelectedItemPosition();
//        int posSup = spinnerSupplier.getSelectedItemPosition();
//        if (posFood < 0 || posFood >= foods.size()) { Toast.makeText(this, "Select food", Toast.LENGTH_SHORT).show(); return; }
//        if (posSup < 0 || posSup >= suppliers.size()) { Toast.makeText(this, "Select supplier", Toast.LENGTH_SHORT).show(); return; }
//        if (selectedDate == null) { Toast.makeText(this, "Pick expiry date", Toast.LENGTH_SHORT).show(); return; }
//        String qtyStr = etQty.getText().toString().trim();
//        if (qtyStr.isEmpty()) { etQty.setError("qty"); return; }
//        int newQty = Integer.parseInt(qtyStr);
//        if (newQty <= 0) { etQty.setError("Invalid"); return; }
//
//        Food newFood = foods.get(posFood);
//        Supplier newSupplier = suppliers.get(posSup);
//
//        ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Saving...");
//        pd.setCancelable(false);
//        pd.show();
//
//        // Steps:
//        // 1) read old exp stock doc (entry.getExpStockId()) -> dec by oldQty
//        // 2) find new exp stock doc for newFood + selectedDate => if exists inc by newQty, else create with newQty
//        // 3) update entry doc fields
//        // We'll do sequential reads then batch commit.
//
//        String oldExpId = entry.getExpStockId();
//        String entryId = entry.getId();
//        com.google.firebase.firestore.DocumentReference oldExpRef = db.collection("food_exp_date_stocks").document(oldExpId);
//        db.runTransaction(tx -> {
//            // transaction not used to modify concurrently here to simplify — we will do read and then batch (since complex)
//            return null;
//        }).addOnCompleteListener(task -> {
//            // simple approach: read old exp, then read find new exp by equality query, then build batch
//            oldExpRef.get().addOnSuccessListener(oldDoc -> {
//                int oldQty = entry.getQty();
//                long curOld = oldDoc.exists() && oldDoc.getLong("stock_amount") != null ? oldDoc.getLong("stock_amount") : 0;
//                int newOldStock = (int) Math.max(0, curOld - oldQty);
//
//                // find new exp doc
//                db.collection("food_exp_date_stocks")
//                        .whereEqualTo("foodId", newFood.getId())
//                        .whereEqualTo("exp_date", selectedDate)
//                        .get()
//                        .addOnSuccessListener(q -> {
//                            try {
//                                WriteBatch batch = db.batch();
//                                // update old exp
//                                batch.update(oldExpRef, "stock_amount", newOldStock);
//
//                                String newExpId;
//                                if (!q.isEmpty()) {
//                                    com.google.firebase.firestore.DocumentSnapshot doc = q.getDocuments().get(0);
//                                    newExpId = doc.getId();
//                                    Long curNew = doc.getLong("stock_amount");
//                                    int curNewInt = curNew == null ? 0 : curNew.intValue();
//                                    int newNewStock = curNewInt + newQty;
//                                    batch.update(db.collection("food_exp_date_stocks").document(newExpId), "stock_amount", newNewStock);
//                                } else {
//                                    newExpId = db.collection("food_exp_date_stocks").document().getId();
//                                    com.google.firebase.firestore.DocumentReference nref = db.collection("food_exp_date_stocks").document(newExpId);
//                                    Map<String, Object> expMap = new HashMap<>();
//                                    expMap.put("id", newExpId);
//                                    expMap.put("foodId", newFood.getId());
//                                    expMap.put("stock_amount", newQty);
//                                    expMap.put("exp_date", selectedDate);
//                                    batch.set(nref, expMap);
//                                }
//
//                                // update entry doc
//                                com.google.firebase.firestore.DocumentReference entryRef = db.collection("stock_entries").document(entryId);
//                                Map<String, Object> entryUpdate = new HashMap<>();
//                                entryUpdate.put("foodId", newFood.getId());
//                                entryUpdate.put("foodName", newFood.getName());
//                                entryUpdate.put("expDate", selectedDate);
//                                entryUpdate.put("qty", newQty);
//                                entryUpdate.put("supplierId", newSupplier.getId());
//                                entryUpdate.put("supplierName", newSupplier.getName());
//                                entryUpdate.put("expStockId", newExpId);
//                                entryUpdate.put("operatorUid", FirebaseAuth.getInstance().getCurrentUser() == null ? "unknown" : FirebaseAuth.getInstance().getCurrentUser().getUid());
//                                entryUpdate.put("operatorName", ""); // fill if you fetched the name
//                                entryUpdate.put("createdAt", Timestamp.now());
//                                batch.update(entryRef, entryUpdate);
//
//                                batch.commit().addOnSuccessListener(aVoid -> {
//                                    pd.dismiss();
//                                    Toast.makeText(this, "Entry updated", Toast.LENGTH_SHORT).show();
//                                    setResult(RESULT_OK);
//                                    finish();
//                                }).addOnFailureListener(er -> {
//                                    pd.dismiss();
//                                    Toast.makeText(this, "Failed: " + er.getMessage(), Toast.LENGTH_SHORT).show();
//                                });
//
//                            } catch (Exception ex) {
//                                pd.dismiss();
//                                Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }).addOnFailureListener(er -> {
//                            pd.dismiss();
//                            Toast.makeText(this, "Lookup new exp failed: " + er.getMessage(), Toast.LENGTH_SHORT).show();
//                        });
//
//            }).addOnFailureListener(er -> {
//                pd.dismiss();
//                Toast.makeText(this, "Old expiry read failed: " + er.getMessage(), Toast.LENGTH_SHORT).show();
//            });
//        });
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.StockEntry;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditStockActivity extends AppCompatActivity {

    private Spinner spinnerFood, spinnerSupplier, spinnerExpDate;
    private EditText etQty, btnPickDate;
    private Button btnUpdate;
    private FirebaseFirestore db;

    private Date selectedDate;
    private ArrayAdapter<String> expDateAdapter;
    private List<Date> expDates = new ArrayList<>();
    private static final String NEW_DATE_OPTION = "Tambah tanggal baru...";

    private String selectedFoodId, selectedSupplierId;
    private Date currentExpDate;
    private int currentQty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stock);

        spinnerFood = findViewById(R.id.spinnerFoodEdit);
        spinnerSupplier = findViewById(R.id.spinnerSupplierEdit);
        spinnerExpDate = findViewById(R.id.spinnerExpDateEdit);
        etQty = findViewById(R.id.etQtyEditStock);
        btnPickDate = findViewById(R.id.btnPickExpDateEditStock);
        btnUpdate = findViewById(R.id.btnSaveEditStock);

        db = FirebaseFirestore.getInstance();

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
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnPickDate.setOnClickListener(v -> showDatePicker());
        btnUpdate.setOnClickListener(v -> updateStock());

        // TODO: Ambil data stock yang mau diedit (misalnya lewat Intent)
        // Contoh dummy:
//        selectedFoodId = "food1";
//        selectedSupplierId = "supplier1";
//        currentExpDate = new Date();
//        currentQty = 10;

        StockEntry entry = (StockEntry) getIntent().getParcelableExtra("entry");
        if (entry != null) {
            selectedFoodId = entry.getFoodId();
            selectedSupplierId = entry.getSupplierId();
            currentExpDate = entry.getExp_date();
            currentQty = entry.getQty();

            etQty.setText(String.valueOf(currentQty));

            loadFoods();       // supaya spinner food terisi
            loadSuppliers();   // supaya spinner supplier terisi
            loadExpDatesAndFill(selectedFoodId, currentExpDate); // set exp date di spinner
        }

        etQty.setText(String.valueOf(currentQty));

        loadFoods();
        loadSuppliers();
        loadExpDatesAndFill(selectedFoodId, currentExpDate);
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
            int selIndex = 0;
            for (DocumentSnapshot doc : query) {
                labels.add(doc.getString("name"));
                ids.add(doc.getId());
                if (doc.getId().equals(selectedFoodId)) selIndex = labels.size() - 1;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFood.setAdapter(adapter);
            spinnerFood.setSelection(selIndex);
        });
    }

    private void loadSuppliers() {
        db.collection("suppliers").get().addOnSuccessListener(query -> {
            List<String> labels = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            int selIndex = 0;
            for (DocumentSnapshot doc : query) {
                labels.add(doc.getString("name"));
                ids.add(doc.getId());
                if (doc.getId().equals(selectedSupplierId)) selIndex = labels.size() - 1;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, labels);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSupplier.setAdapter(adapter);
            spinnerSupplier.setSelection(selIndex);
        });
    }

    private void loadExpDatesAndFill(String foodId, Date currentExp) {
        db.collection("food_exp_date_stocks")
                .whereEqualTo("foodId", foodId)
                .get()
                .addOnSuccessListener(q -> {
                    expDates.clear();
                    List<String> labels = new ArrayList<>();
                    int selIndex = 0;
                    for (DocumentSnapshot d : q.getDocuments()) {
                        Date exp = d.getDate("exp_date");
                        if (exp != null) {
                            expDates.add(exp);
                            String label = android.text.format.DateFormat.format("yyyy-MM-dd", exp).toString();
                            labels.add(label);
                            if (currentExp != null && currentExp.equals(exp)) {
                                selIndex = labels.size() - 1;
                                selectedDate = exp;
                            }
                        }
                    }
                    labels.add(NEW_DATE_OPTION);
                    expDateAdapter.clear();
                    expDateAdapter.addAll(labels);
                    expDateAdapter.notifyDataSetChanged();
                    spinnerExpDate.setSelection(selIndex);
                });
    }

//    private void updateStock() {
//        String qtyStr = etQty.getText().toString().trim();
//        if (TextUtils.isEmpty(qtyStr) || selectedDate == null || selectedFoodId == null || selectedSupplierId == null) {
//            Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        int qty = Integer.parseInt(qtyStr);
//        // TODO: Update Firestore sesuai struktur kamu
//        Toast.makeText(this, "Stock berhasil diupdate", Toast.LENGTH_SHORT).show();
//        finish();
//    }

    private void updateStock() {
        String qtyStr = etQty.getText().toString().trim();
        if (TextUtils.isEmpty(qtyStr) || selectedDate == null || selectedFoodId == null || selectedSupplierId == null) {
            Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        int newQty = Integer.parseInt(qtyStr);

        // Misal dapat ID entry yang diedit lewat Intent
        String entryId = getIntent().getStringExtra("entryId");
        if (entryId == null) {
            Toast.makeText(this, "ID Entry tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("entry_stocks").document(entryId).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    String oldFoodId = doc.getString("foodId");
                    Date oldExpDate = doc.getDate("exp_date");
                    int oldQty = doc.getLong("qty").intValue();

                    // 🔹 Step 1: Kurangi stok lama
                    db.collection("food_exp_date_stocks")
                            .whereEqualTo("foodId", oldFoodId)
                            .whereEqualTo("exp_date", oldExpDate)
                            .get()
                            .addOnSuccessListener(q -> {
                                if (!q.isEmpty()) {
                                    DocumentSnapshot stockDoc = q.getDocuments().get(0);
                                    String stockId = stockDoc.getId();
                                    int currentStock = stockDoc.getLong("stock_amount").intValue();
                                    int newStock = Math.max(0, currentStock - oldQty);
                                    db.collection("food_exp_date_stocks").document(stockId)
                                            .update("stock_amount", newStock);
                                }

                                // 🔹 Step 2: Tambah stok baru
                                db.collection("food_exp_date_stocks")
                                        .whereEqualTo("foodId", selectedFoodId)
                                        .whereEqualTo("exp_date", selectedDate)
                                        .get()
                                        .addOnSuccessListener(q2 -> {
                                            if (q2.isEmpty()) {
                                                String newStockId = db.collection("food_exp_date_stocks").document().getId();
                                                java.util.Map<String, Object> stockData = new java.util.HashMap<>();
                                                stockData.put("id", newStockId);
                                                stockData.put("foodId", selectedFoodId);
                                                stockData.put("stock_amount", newQty);
                                                stockData.put("exp_date", selectedDate);

                                                db.collection("food_exp_date_stocks").document(newStockId)
                                                        .set(stockData);
                                            } else {
                                                DocumentSnapshot stockDoc = q2.getDocuments().get(0);
                                                String stockId = stockDoc.getId();
                                                int currentStock = stockDoc.getLong("stock_amount").intValue();
                                                int updatedStock = currentStock + newQty;
                                                db.collection("food_exp_date_stocks").document(stockId)
                                                        .update("stock_amount", updatedStock);
                                            }

                                            // 🔹 Step 3: Update entry_stocks
                                            java.util.Map<String, Object> entryUpdate = new java.util.HashMap<>();
                                            entryUpdate.put("foodId", selectedFoodId);
                                            entryUpdate.put("exp_date", selectedDate);
                                            entryUpdate.put("qty", newQty);
                                            entryUpdate.put("supplierId", selectedSupplierId);
                                            entryUpdate.put("timestamp", new java.util.Date());

                                            db.collection("entry_stocks").document(entryId)
                                                    .update(entryUpdate)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(this, "Stock berhasil diupdate", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e ->
                                                            Toast.makeText(this, "Gagal update stock", Toast.LENGTH_SHORT).show());
                                        });
                            });
                });
    }
}
