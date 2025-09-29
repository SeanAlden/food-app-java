//package com.example.foodapp_java.page;
//
//import android.app.DatePickerDialog;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.OpenableColumns;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.foodapp_java.R;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.WriteBatch;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class AddFoodActivity extends AppCompatActivity {
//
//    private EditText etName, etPrice, etDesc;
//    private ImageView ivPreview;
//    private LinearLayout containerExpRows;
//    private Button btnAddRow, btnSave;
//    private Uri selectedImageUri;
//    private String savedImagePath;
//    private FirebaseFirestore db;
//    private LayoutInflater inflater;
//    private static final String TAG = "AddFoodActivity";
//
//    private ActivityResultLauncher<String> pickImageLauncher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_food);
//
//        etName = findViewById(R.id.etName);
//        etPrice = findViewById(R.id.etPrice);
//        etDesc = findViewById(R.id.etDesc);
//        ivPreview = findViewById(R.id.ivPreview);
//        containerExpRows = findViewById(R.id.containerExpRows);
//        btnAddRow = findViewById(R.id.btnAddRow);
//        btnSave = findViewById(R.id.btnSave);
//        inflater = LayoutInflater.from(this);
//        db = FirebaseFirestore.getInstance();
//
//        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
//                (ActivityResultCallback<Uri>) uri -> {
//                    if (uri != null) {
//                        selectedImageUri = uri;
//                        try {
//                            savedImagePath = saveImageToInternal(uri);
//                            ivPreview.setImageBitmap(BitmapFactory.decodeFile(savedImagePath));
//                        } catch (Exception e) {
//                            Log.e(TAG, "save image failed", e);
//                            Toast.makeText(this, "Failed save image", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//        findViewById(R.id.btnPickImage).setOnClickListener(v -> pickImageLauncher.launch("image/*"));
//
//        btnAddRow.setOnClickListener(v -> addExpRow(null, 0));
//
//        // add one row by default
//        addExpRow(null, 0);
//
//        btnSave.setOnClickListener(v -> saveFood());
//    }
//
//    private void addExpRow(Date preDate, int preStock) {
//        View row = inflater.inflate(R.layout.item_exp_input, containerExpRows, false);
//        EditText etStock = row.findViewById(R.id.etStock);
//        Button btnPickDate = row.findViewById(R.id.btnPickDate);
//        Button btnRemove = row.findViewById(R.id.btnRemove);
//        final long[] pickedMillis = { preDate == null ? -1 : preDate.getTime() };
//
//        if (preStock > 0) etStock.setText(String.valueOf(preStock));
//        if (preDate != null) btnPickDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", preDate));
//
//        btnPickDate.setOnClickListener(v -> {
//            Calendar c = Calendar.getInstance();
//            DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
//                Calendar sel = Calendar.getInstance();
//                sel.set(year, month, dayOfMonth, 0, 0, 0);
//                pickedMillis[0] = sel.getTimeInMillis();
//                btnPickDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", new Date(pickedMillis[0])));
//            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//            dp.show();
//        });
//
//        btnRemove.setOnClickListener(v -> containerExpRows.removeView(row));
//
//        containerExpRows.addView(row);
//    }
//
//    private String saveImageToInternal(Uri uri) throws Exception {
//        InputStream is = getContentResolver().openInputStream(uri);
//        File dir = new File(getFilesDir(), "images");
//        if (!dir.exists()) dir.mkdirs();
//        String filename = "food_" + UUID.randomUUID().toString() + ".jpg";
//        File out = new File(dir, filename);
//        FileOutputStream fos = new FileOutputStream(out);
//        byte[] buf = new byte[1024];
//        int len;
//        while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
//        fos.close();
//        is.close();
//        return out.getAbsolutePath();
//    }
//
////    private void saveFood() {
////        String name = etName.getText().toString().trim();
////        String priceStr = etPrice.getText().toString().trim();
////        String desc = etDesc.getText().toString().trim();
////
////        if (name.isEmpty()) { etName.setError("Name required"); return; }
////        if (priceStr.isEmpty()) { etPrice.setError("Price required"); return; }
////        double price;
////        try { price = Double.parseDouble(priceStr); } catch (Exception e) { etPrice.setError("Invalid price"); return; }
////
////        // create food doc
////        String foodId = db.collection("foods").document().getId();
////        Map<String, Object> foodMap = new HashMap<>();
////        foodMap.put("id", foodId);
////        foodMap.put("name", name);
////        foodMap.put("price", price);
////        foodMap.put("description", desc);
////        foodMap.put("status", "active");
////        foodMap.put("imagePath", savedImagePath == null ? "" : savedImagePath);
////
////        db.collection("foods").document(foodId).set(foodMap)
////                .addOnSuccessListener(aVoid -> {
////                    // create exp rows
////                    int childCount = containerExpRows.getChildCount();
////                    for (int i = 0; i < childCount; i++) {
////                        View row = containerExpRows.getChildAt(i);
////                        EditText etStock = row.findViewById(R.id.etStock);
////                        Button btnPickDate = row.findViewById(R.id.btnPickDate);
////                        String stockStr = etStock.getText().toString().trim();
////                        if (stockStr.isEmpty()) continue;
////                        int stock = Integer.parseInt(stockStr);
////
////                        Object tag = btnPickDate.getText();
////                        String dateText = tag == null ? "" : btnPickDate.getText().toString();
////                        // We stored date on button text; if not selected, skip
////                        if (dateText == null || dateText.equals("Pick date") || dateText.isEmpty()) continue;
////
////                        try {
////                            // parse yyyy-MM-dd from button text
////                            String[] parts = dateText.split("-");
////                            int y = Integer.parseInt(parts[0]);
////                            int m = Integer.parseInt(parts[1]) - 1;
////                            int d = Integer.parseInt(parts[2]);
////                            Calendar c = Calendar.getInstance();
////                            c.set(y, m, d, 0, 0, 0);
////                            Date expDate = c.getTime();
////
////                            String expId = db.collection("food_exp_date_stocks").document().getId();
////                            Map<String, Object> expMap = new HashMap<>();
////                            expMap.put("id", expId);
////                            expMap.put("foodId", foodId);
////                            expMap.put("stock_amount", stock);
////                            expMap.put("exp_date", expDate);
////
////                            db.collection("food_exp_date_stocks").document(expId).set(expMap);
////                        } catch (Exception ex) {
////                            Log.w(TAG, "invalid date format", ex);
////                        }
////                    }
////
////                    Toast.makeText(this, "Food saved", Toast.LENGTH_SHORT).show();
////
////                    setResult(RESULT_OK);
////
////                    finish(); // back to list
////                })
////                .addOnFailureListener(e -> {
////                    Toast.makeText(this, "Failed to save food: " + e.getMessage(), Toast.LENGTH_SHORT).show();
////                });
////    }
//
//    private void saveFood() {
//        String name = etName.getText().toString().trim();
//        String priceStr = etPrice.getText().toString().trim();
//        String desc = etDesc.getText().toString().trim();
//
//        if (name.isEmpty()) { etName.setError("Name required"); return; }
//        if (priceStr.isEmpty()) { etPrice.setError("Price required"); return; }
//
//        double price;
//        try { price = Double.parseDouble(priceStr); }
//        catch (Exception e) { etPrice.setError("Invalid price"); return; }
//
//        ProgressDialog progress = new ProgressDialog(this);
//        progress.setMessage("Saving...");
//        progress.setCancelable(false);
//        progress.show();
//
//        new Thread(() -> {
//            try {
//                // save image kalau ada
//                String finalImagePath = "";
//                if (selectedImageUri != null) {
//                    finalImagePath = saveImageToInternal(selectedImageUri);
//                }
//
//                // buat batch
//                WriteBatch batch = db.batch();
//
//                String foodId = db.collection("foods").document().getId();
//                DocumentReference foodRef = db.collection("foods").document(foodId);
//
//                Map<String, Object> foodMap = new HashMap<>();
//                foodMap.put("id", foodId);
//                foodMap.put("name", name);
//                foodMap.put("price", price);
//                foodMap.put("description", desc);
//                foodMap.put("status", "active");
//                foodMap.put("imagePath", finalImagePath);
//                batch.set(foodRef, foodMap);
//
//                // exp rows
//                int childCount = containerExpRows.getChildCount();
//                for (int i = 0; i < childCount; i++) {
//                    View row = containerExpRows.getChildAt(i);
//                    EditText etStock = row.findViewById(R.id.etStock);
//                    Button btnPickDate = row.findViewById(R.id.btnPickDate);
//
//                    String stockStr = etStock.getText().toString().trim();
//                    if (stockStr.isEmpty()) continue;
//                    int stock = Integer.parseInt(stockStr);
//
//                    String dateText = btnPickDate.getText().toString();
//                    if (dateText.equals("Pick date") || dateText.isEmpty()) continue;
//
//                    String[] parts = dateText.split("-");
//                    int y = Integer.parseInt(parts[0]);
//                    int m = Integer.parseInt(parts[1]) - 1;
//                    int d = Integer.parseInt(parts[2]);
//
//                    Calendar c = Calendar.getInstance();
//                    c.set(y, m, d, 0, 0, 0);
//                    Date expDate = c.getTime();
//
//                    String expId = db.collection("food_exp_date_stocks").document().getId();
//                    DocumentReference expRef = db.collection("food_exp_date_stocks").document(expId);
//
//                    Map<String, Object> expMap = new HashMap<>();
//                    expMap.put("id", expId);
//                    expMap.put("foodId", foodId);
//                    expMap.put("stock_amount", stock);
//                    expMap.put("exp_date", expDate);
//
//                    batch.set(expRef, expMap);
//                }
//
//                // commit batch
//                batch.commit()
//                        .addOnSuccessListener(aVoid -> {
//                            progress.dismiss();
//                            runOnUiThread(() -> {
//                                Toast.makeText(this, "Food saved", Toast.LENGTH_SHORT).show();
//                                setResult(RESULT_OK);
//                                finish();
//                            });
//                        })
//                        .addOnFailureListener(e -> {
//                            progress.dismiss();
//                            runOnUiThread(() ->
//                                    Toast.makeText(this, "Failed to save food: " + e.getMessage(), Toast.LENGTH_SHORT).show()
//                            );
//                        });
//
//            } catch (Exception ex) {
//                progress.dismiss();
//                runOnUiThread(() ->
//                        Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show()
//                );
//            }
//        }).start();
//    }
//
//}

package com.example.foodapp_java.page;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp_java.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddFoodActivity extends AppCompatActivity {

    private EditText etName, etPrice, etDesc;
    private ImageView ivPreview;
    private LinearLayout containerExpRows;
    private Button btnAddRow, btnSave;
    private Uri selectedImageUri;
    private String savedImagePath;
    private FirebaseFirestore db;
    private LayoutInflater inflater;
    private static final String TAG = "AddFoodActivity";

    private ActivityResultLauncher<String> pickImageLauncher;

    // NEW: category spinner + backing lists
    private Spinner spinnerCategory;
    private final List<String> categoryNames = new ArrayList<>();
    private final List<String> categoryIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etDesc = findViewById(R.id.etDesc);
        ivPreview = findViewById(R.id.ivPreview);
        containerExpRows = findViewById(R.id.containerExpRows);
        btnAddRow = findViewById(R.id.btnAddRow);
        btnSave = findViewById(R.id.btnSave);
        inflater = LayoutInflater.from(this);
        db = FirebaseFirestore.getInstance();

        spinnerCategory = findViewById(R.id.spinnerCategory);

        // load categories for spinner
        loadCategories();

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                (ActivityResultCallback<Uri>) uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        try {
                            savedImagePath = saveImageToInternal(uri);
                            ivPreview.setImageBitmap(BitmapFactory.decodeFile(savedImagePath));
                        } catch (Exception e) {
                            Log.e(TAG, "save image failed", e);
                            Toast.makeText(this, "Failed save image", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        findViewById(R.id.btnPickImage).setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        btnAddRow.setOnClickListener(v -> addExpRow(null, 0));

        // add one row by default
        addExpRow(null, 0);

        btnSave.setOnClickListener(v -> saveFood());
    }

    private void loadCategories() {
        categoryNames.clear();
        categoryIds.clear();
        // query categories
        db.collection("categories")
                .orderBy("name")
                .get()
                .addOnSuccessListener(q -> {
                    for (QueryDocumentSnapshot doc : q) {
                        String name = doc.getString("name");
                        categoryNames.add(name == null ? "Unnamed" : name);
                        categoryIds.add(doc.getId());
                    }
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddFoodActivity.this,
                                android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "failed load categories", e);
                    runOnUiThread(() -> {
                        // fallback: empty adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddFoodActivity.this,
                                android.R.layout.simple_spinner_item, new ArrayList<>());
                        spinnerCategory.setAdapter(adapter);
                    });
                });
    }

    private void addExpRow(Date preDate, int preStock) {
        View row = inflater.inflate(R.layout.item_exp_input, containerExpRows, false);
        EditText etStock = row.findViewById(R.id.etStock);
        Button btnPickDate = row.findViewById(R.id.btnPickDate);
        Button btnRemove = row.findViewById(R.id.btnRemove);
        final long[] pickedMillis = { preDate == null ? -1 : preDate.getTime() };

        if (preStock > 0) etStock.setText(String.valueOf(preStock));
        if (preDate != null) btnPickDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", preDate));

        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                Calendar sel = Calendar.getInstance();
                sel.set(year, month, dayOfMonth, 0, 0, 0);
                pickedMillis[0] = sel.getTimeInMillis();
                btnPickDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", new Date(pickedMillis[0])));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            dp.show();
        });

        btnRemove.setOnClickListener(v -> containerExpRows.removeView(row));

        containerExpRows.addView(row);
    }

    private String saveImageToInternal(Uri uri) throws Exception {
        InputStream is = getContentResolver().openInputStream(uri);
        File dir = new File(getFilesDir(), "images");
        if (!dir.exists()) dir.mkdirs();
        String filename = "food_" + UUID.randomUUID().toString() + ".jpg";
        File out = new File(dir, filename);
        FileOutputStream fos = new FileOutputStream(out);
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
        fos.close();
        is.close();
        return out.getAbsolutePath();
    }

    private void saveFood() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (name.isEmpty()) { etName.setError("Name required"); return; }
        if (priceStr.isEmpty()) { etPrice.setError("Price required"); return; }

        double price;
        try { price = Double.parseDouble(priceStr); }
        catch (Exception e) { etPrice.setError("Invalid price"); return; }

        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Saving...");
        progress.setCancelable(false);
        progress.show();

        new Thread(() -> {
            try {
                // save image kalau ada
                String finalImagePath = "";
                if (selectedImageUri != null) {
                    finalImagePath = saveImageToInternal(selectedImageUri);
                }

                // buat batch
                WriteBatch batch = db.batch();

                String foodId = db.collection("foods").document().getId();
                DocumentReference foodRef = db.collection("foods").document(foodId);

                Map<String, Object> foodMap = new HashMap<>();
                foodMap.put("id", foodId);
                foodMap.put("name", name);
                foodMap.put("price", price);
                foodMap.put("description", desc);
                foodMap.put("status", "active");
                foodMap.put("imagePath", finalImagePath);

                // NEW: category id (if available)
                String selectedCategoryId = null;
                int selIndex = spinnerCategory.getSelectedItemPosition();
                if (selIndex >= 0 && selIndex < categoryIds.size()) {
                    selectedCategoryId = categoryIds.get(selIndex);
                }
                if (selectedCategoryId != null) {
                    foodMap.put("category_id", selectedCategoryId);
                }

                batch.set(foodRef, foodMap);

                // exp rows
                int childCount = containerExpRows.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View row = containerExpRows.getChildAt(i);
                    EditText etStock = row.findViewById(R.id.etStock);
                    Button btnPickDate = row.findViewById(R.id.btnPickDate);

                    String stockStr = etStock.getText().toString().trim();
                    if (stockStr.isEmpty()) continue;
                    int stock = Integer.parseInt(stockStr);

                    String dateText = btnPickDate.getText().toString();
                    if (dateText.equals("Pick date") || dateText.isEmpty()) continue;

                    String[] parts = dateText.split("-");
                    int y = Integer.parseInt(parts[0]);
                    int m = Integer.parseInt(parts[1]) - 1;
                    int d = Integer.parseInt(parts[2]);

                    Calendar c = Calendar.getInstance();
                    c.set(y, m, d, 0, 0, 0);
                    Date expDate = c.getTime();

                    String expId = db.collection("food_exp_date_stocks").document().getId();
                    DocumentReference expRef = db.collection("food_exp_date_stocks").document(expId);

                    Map<String, Object> expMap = new HashMap<>();
                    expMap.put("id", expId);
                    expMap.put("foodId", foodId);
                    expMap.put("stock_amount", stock);
                    expMap.put("exp_date", expDate);

                    batch.set(expRef, expMap);
                }

                // commit batch
                batch.commit()
                        .addOnSuccessListener(aVoid -> {
                            progress.dismiss();
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Food saved", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            });
                        })
                        .addOnFailureListener(e -> {
                            progress.dismiss();
                            runOnUiThread(() ->
                                    Toast.makeText(this, "Failed to save food: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                        });

            } catch (Exception ex) {
                progress.dismiss();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + ex.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

}
