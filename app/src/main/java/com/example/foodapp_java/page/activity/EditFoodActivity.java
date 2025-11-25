//package com.example.foodapp_java.page;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.foodapp_java.R;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class EditFoodActivity extends AppCompatActivity {
//
//    private static final int PICK_IMAGE_REQUEST = 1;
//
//    private EditText etName, etPrice, etDescription;
//    private ImageView ivFoodImage;
//    private Button btnChooseImage, btnSave;
//    private Uri imageUri;
//
//    private FirebaseFirestore db;
//    private String foodId; // diterima dari Intent
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_food);
//
//        etName = findViewById(R.id.etEditFoodName);
//        etPrice = findViewById(R.id.etEditFoodPrice);
//        etDescription = findViewById(R.id.etEditFoodDescription);
//        ivFoodImage = findViewById(R.id.ivEditFoodImage);
//        btnChooseImage = findViewById(R.id.btnChooseImage);
//        btnSave = findViewById(R.id.btnSaveFood);
//
//        db = FirebaseFirestore.getInstance();
//
//        // Ambil data dari Intent
//        foodId = getIntent().getStringExtra("foodId");
//        String name = getIntent().getStringExtra("name");
//        String price = getIntent().getStringExtra("price");
//        String description = getIntent().getStringExtra("description");
//
//        String imagePath = getIntent().getStringExtra("imagePath");
//        if (imagePath != null && !imagePath.isEmpty()) {
//            ivFoodImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
//        }
//
//        etName.setText(name);
//        etPrice.setText(price);
//        etDescription.setText(description);
//
//        btnChooseImage.setOnClickListener(v -> openFileChooser());
//
//        btnSave.setOnClickListener(v -> saveFoodData());
//    }
//
//    private void openFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
//                && data != null && data.getData() != null) {
//            imageUri = data.getData();
//
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                ivFoodImage.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void saveFoodData() {
//        String name = etName.getText().toString().trim();
//        String price = etPrice.getText().toString().trim();
//        String description = etDescription.getText().toString().trim();
//
//        if (name.isEmpty() || price.isEmpty() || description.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Map<String, Object> foodMap = new HashMap<>();
//        foodMap.put("name", name);
//        foodMap.put("price", price);
//        foodMap.put("description", description);
//
//        db.collection("foods").document(foodId)
//                .update(foodMap)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Food updated successfully", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//}

//package com.example.foodapp_java.page;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.dataClass.Food;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class EditFoodActivity extends AppCompatActivity {
//
//    private static final int PICK_IMAGE_REQUEST = 1;
//
//    private EditText etName, etPrice, etDescription;
//    private ImageView ivFoodImage;
//    private Button btnChooseImage, btnSave;
//    private Uri imageUri;
//
//    private FirebaseFirestore db;
//    private Food food;  // Parcelable object
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_food);
//
//        etName = findViewById(R.id.etEditFoodName);
//        etPrice = findViewById(R.id.etEditFoodPrice);
//        etDescription = findViewById(R.id.etEditFoodDescription);
//        ivFoodImage = findViewById(R.id.ivEditFoodImage);
//        btnChooseImage = findViewById(R.id.btnChooseImage);
//        btnSave = findViewById(R.id.btnSaveFood);
//
//        db = FirebaseFirestore.getInstance();
//
//        // ✅ Ambil data dari Intent (Parcelable)
//        food = getIntent().getParcelableExtra("food");
//        if (food != null) {
//            etName.setText(food.getName());
//            etPrice.setText(String.valueOf(food.getPrice()));
//            etDescription.setText(food.getDescription());
//
//            if (food.getImagePath() != null && !food.getImagePath().isEmpty()) {
//                ivFoodImage.setImageBitmap(BitmapFactory.decodeFile(food.getImagePath()));
//            }
//        }
//
//        btnChooseImage.setOnClickListener(v -> openFileChooser());
//
//        btnSave.setOnClickListener(v -> saveFoodData());
//    }
//
//    private void openFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
//                && data != null && data.getData() != null) {
//            imageUri = data.getData();
//
//            try {
//                ivFoodImage.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void saveFoodData() {
//        String name = etName.getText().toString().trim();
//        String priceStr = etPrice.getText().toString().trim();
//        String description = etDescription.getText().toString().trim();
//
//        if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        double price = Double.parseDouble(priceStr);
//
//        Map<String, Object> foodMap = new HashMap<>();
//        foodMap.put("name", name);
//        foodMap.put("price", price);
//        foodMap.put("description", description);
//
//        db.collection("foods").document(food.getId())
//                .update(foodMap)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Food updated successfully", Toast.LENGTH_SHORT).show();
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//}

//package com.example.foodapp_java.page;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.dataClass.Food;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class EditFoodActivity extends AppCompatActivity {
//
//    private static final int PICK_IMAGE_REQUEST = 1;
//
//    private EditText etName, etPrice, etDescription;
//    private ImageView ivFoodImage;
//    private Button btnChooseImage, btnSave;
//    private Uri imageUri;
//    private String newImagePath = null; // path baru kalau gambar diganti
//
//    private FirebaseFirestore db;
//    private Food food;  // Parcelable object
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_edit_food);
//
//        etName = findViewById(R.id.etEditFoodName);
//        etPrice = findViewById(R.id.etEditFoodPrice);
//        etDescription = findViewById(R.id.etEditFoodDescription);
//        ivFoodImage = findViewById(R.id.ivEditFoodImage);
//        btnChooseImage = findViewById(R.id.btnChooseImage);
//        btnSave = findViewById(R.id.btnSaveFood);
//
//        db = FirebaseFirestore.getInstance();
//
//        // ✅ Ambil data dari Intent (Parcelable)
//        food = getIntent().getParcelableExtra("food");
//        if (food != null) {
//            etName.setText(food.getName());
//            etPrice.setText(String.valueOf(food.getPrice()));
//            etDescription.setText(food.getDescription());
//
////            if (food.getImagePath() != null && !food.getImagePath().isEmpty()) {
////                ivFoodImage.setImageBitmap(BitmapFactory.decodeFile(food.getImagePath()));
////            }
//
//            if (food.getImagePath() != null && !food.getImagePath().isEmpty()) {
//                File f = new File(food.getImagePath());
//                if (f.exists()) {
//                    ivFoodImage.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
//                } else {
//                    ivFoodImage.setImageResource(R.drawable.food);
//                }
//            } else {
//                ivFoodImage.setImageResource(R.drawable.food);
//            }
//
//        }
//
//        btnChooseImage.setOnClickListener(v -> openFileChooser());
//        btnSave.setOnClickListener(v -> saveFoodData());
//    }
//
//    private void openFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
//                && data != null && data.getData() != null) {
//            imageUri = data.getData();
//
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                ivFoodImage.setImageBitmap(bitmap);
//
//                // ✅ Simpan gambar ke local storage
//                newImagePath = saveImageToLocal(bitmap, food.getId());
//                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // ✅ Simpan gambar ke local storage & return path
//    private String saveImageToLocal(Bitmap bitmap, String foodId) {
//        File dir = new File(getFilesDir(), "food_images");
//        if (!dir.exists()) dir.mkdirs();
//
//        File file = new File(dir, "food_" + foodId + ".jpg");
//        try (FileOutputStream fos = new FileOutputStream(file)) {
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
//            fos.flush();
//            return file.getAbsolutePath();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    private void saveFoodData() {
//        String name = etName.getText().toString().trim();
//        String priceStr = etPrice.getText().toString().trim();
//        String description = etDescription.getText().toString().trim();
//
//        if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        double price = Double.parseDouble(priceStr);
//
//        Map<String, Object> foodMap = new HashMap<>();
//        foodMap.put("name", name);
//        foodMap.put("price", price);
//        foodMap.put("description", description);
//
//        // ✅ kalau ada gambar baru → update imagePath juga
//        if (newImagePath != null) {
//            foodMap.put("imagePath", newImagePath);
//        }
//
//        db.collection("foods").document(food.getId())
//                .update(foodMap)
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(this, "Food updated successfully", Toast.LENGTH_SHORT).show();
//
//                    setResult(RESULT_OK);
//
//                    finish();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }
//}

package com.example.foodapp_java.page.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Food;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditFoodActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etName, etPrice, etDescription;
    private ImageView ivFoodImage;
    private Button btnChooseImage, btnSave;
    private String newImagePath = null; // path baru kalau gambar diganti

    private FirebaseFirestore db;
    private Food food;  // Parcelable object

    // NEW: category spinner data
    private Spinner spinnerCategory;
    private final List<String> categoryNames = new ArrayList<>();
    private final List<String> categoryIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarEditFood);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(" ");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etName = findViewById(R.id.etEditFoodName);
        etPrice = findViewById(R.id.etEditFoodPrice);
        etDescription = findViewById(R.id.etEditFoodDescription);
        ivFoodImage = findViewById(R.id.ivEditFoodImage);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSaveFood);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        db = FirebaseFirestore.getInstance();

        // Ambil data dari Intent (Parcelable)
        food = getIntent().getParcelableExtra("food");
        if (food != null) {
            etName.setText(food.getName());
            etPrice.setText(String.valueOf(food.getPrice()));
            etDescription.setText(food.getDescription());

            if (food.getImagePath() != null && !food.getImagePath().isEmpty()) {
                File f = new File(food.getImagePath());
                if (f.exists()) {
                    ivFoodImage.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
                } else {
                    ivFoodImage.setImageResource(R.drawable.food);
                }
            } else {
                ivFoodImage.setImageResource(R.drawable.food);
            }
        }

        // load categories then set spinner selection (if food has category)
        loadCategoriesAndSelect();

        btnChooseImage.setOnClickListener(v -> openFileChooser());
        btnSave.setOnClickListener(v -> saveFoodData());
    }

    private void loadCategoriesAndSelect() {
        categoryNames.clear();
        categoryIds.clear();
        db.collection("categories")
                .orderBy("name")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        categoryNames.add(doc.getString("name") == null ? "Unnamed" : doc.getString("name"));
                        categoryIds.add(doc.getId());
                    }
                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(EditFoodActivity.this,
                                android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);

                        // set selection if food has categoryId
                        if (food != null && food.getCategoryId() != null) {
                            int idx = categoryIds.indexOf(food.getCategoryId());
                            if (idx >= 0) spinnerCategory.setSelection(idx);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.w("EditFoodActivity", "failed to load categories", e);
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivFoodImage.setImageBitmap(bitmap);

                // Simpan gambar ke local storage
                newImagePath = saveImageToLocal(bitmap, food.getId());

                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Simpan gambar ke local storage & return path
    private String saveImageToLocal(Bitmap bitmap, String foodId) {
        File dir = new File(getFilesDir(), "food_images");
        if (!dir.exists()) dir.mkdirs();

        File file = new File(dir, "food_" + foodId + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveFoodData() {
        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        Map<String, Object> foodMap = new HashMap<>();
        foodMap.put("name", name);
        foodMap.put("price", price);
        foodMap.put("description", description);

        // kalau ada gambar baru → update imagePath juga
        if (newImagePath != null) {
            foodMap.put("imagePath", newImagePath);
        }

        // NEW: update category_id if selected
        int sel = spinnerCategory.getSelectedItemPosition();
        if (sel >= 0 && sel < categoryIds.size()) {
            foodMap.put("category_id", categoryIds.get(sel));
        }

        db.collection("foods").document(food.getId())
                .update(foodMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Food updated successfully", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);

                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}



