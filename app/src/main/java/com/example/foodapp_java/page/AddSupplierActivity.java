// path: app/src/main/java/com/example/foodapp_java/page/AddSupplierActivity.java
package com.example.foodapp_java.page;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp_java.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AddSupplierActivity extends AppCompatActivity {
    private static final String TAG = "AddSupplierActivity";

    private EditText etName, etCode, etPhone, etAddress, etDesc;
    private ImageView ivPreview;
    private Button btnPick, btnSave;
    private Uri pickedUri;
    private String savedImagePath;
    private FirebaseFirestore db;
    private ActivityResultLauncher<String> pickLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_supplier);

        etName = findViewById(R.id.etSupplierName);
        etCode = findViewById(R.id.etSupplierCode);
        etPhone = findViewById(R.id.etSupplierPhone);
        etAddress = findViewById(R.id.etSupplierAddress);
        etDesc = findViewById(R.id.etSupplierDesc);
        ivPreview = findViewById(R.id.ivSupplierPreview);
        btnPick = findViewById(R.id.btnPickSupplierImage);
        btnSave = findViewById(R.id.btnSaveSupplier);

        db = FirebaseFirestore.getInstance();

        pickLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                pickedUri = uri;
                // show preview quickly (not persisted yet)
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    ivPreview.setImageBitmap(BitmapFactory.decodeStream(is));
                    if (is != null) is.close();
                } catch (Exception ex) { Log.w(TAG, "preview failed", ex); }
            }
        });

        btnPick.setOnClickListener(v -> pickLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String code = etCode.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (name.isEmpty()) { etName.setError("Required"); return; }
            if (code.isEmpty()) { etCode.setError("Required"); return; }

            ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("Saving supplier...");
            progress.setCancelable(false);
            progress.show();

            new Thread(() -> {
                try {
                    // create id first
                    String supplierId = db.collection("suppliers").document().getId();

                    // save image (if any)
                    String imgPath = "";
                    if (pickedUri != null) {
                        imgPath = saveImageToInternal(pickedUri, "supplier_" + supplierId);
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", supplierId);
                    map.put("name", name);
                    map.put("code", code);
                    map.put("phone", phone);
                    map.put("address", address);
                    map.put("description", desc);
                    map.put("image", imgPath == null ? "" : imgPath);

                    DocumentReference ref = db.collection("suppliers").document(supplierId);
                    ref.set(map).addOnSuccessListener(aVoid -> {
                        progress.dismiss();
                        setResult(RESULT_OK);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Supplier saved", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }).addOnFailureListener(e -> {
                        progress.dismiss();
                        runOnUiThread(() -> Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    });

                } catch (Exception e) {
                    progress.dismiss();
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }

    private String saveImageToInternal(Uri uri, String namePrefix) throws Exception {
        InputStream is = getContentResolver().openInputStream(uri);
        File dir = new File(getFilesDir(), "supplier_images");
        if (!dir.exists()) dir.mkdirs();
        String filename = namePrefix + ".jpg";
        File out = new File(dir, filename);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            fos.flush();
        } finally {
            if (is != null) is.close();
        }
        return out.getAbsolutePath();
    }
}
