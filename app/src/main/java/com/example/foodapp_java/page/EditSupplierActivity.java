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
import com.example.foodapp_java.dataClass.Supplier;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditSupplierActivity extends AppCompatActivity {
    private EditText etName, etCode, etPhone, etAddress, etDesc;
    private ImageView ivPreview;
    private Button btnPick, btnSave;
    private Uri pickedUri;
    private Supplier supplier;
    private FirebaseFirestore db;
    private ActivityResultLauncher<String> pickLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_supplier);

        etName = findViewById(R.id.etSupplierNameEdit);
        etCode = findViewById(R.id.etSupplierCodeEdit);
        etPhone = findViewById(R.id.etSupplierPhoneEdit);
        etAddress = findViewById(R.id.etSupplierAddressEdit);
        etDesc = findViewById(R.id.etSupplierDescEdit);
        ivPreview = findViewById(R.id.ivSupplierPreviewEdit);
        btnPick = findViewById(R.id.btnPickSupplierImageEdit);
        btnSave = findViewById(R.id.btnSaveSupplierEdit);

        db = FirebaseFirestore.getInstance();

        supplier = getIntent().getParcelableExtra("supplier");
        if (supplier != null) {
            etName.setText(supplier.getName());
            etCode.setText(supplier.getCode());
            etPhone.setText(supplier.getPhone());
            etAddress.setText(supplier.getAddress());
            etDesc.setText(supplier.getDescription());

            String path = supplier.getImage();
            if (path != null && !path.isEmpty()) {
                File f = new File(path);
                if (f.exists()) ivPreview.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
                else ivPreview.setImageResource(R.drawable.supplier);
            } else ivPreview.setImageResource(R.drawable.supplier);
        }

        pickLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                pickedUri = uri;
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    ivPreview.setImageBitmap(BitmapFactory.decodeStream(is));
                    if (is != null) is.close();
                } catch (Exception ex) { Log.w("EditSupplier", "preview failed", ex); }
            }
        });

        btnPick.setOnClickListener(v -> pickLauncher.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            if (supplier == null) return;
            String name = etName.getText().toString().trim();
            String code = etCode.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (name.isEmpty()) { etName.setError("Required"); return; }

            ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("Updating...");
            progress.setCancelable(false);
            progress.show();

            new Thread(() -> {
                try {
                    String newPath = supplier.getImage();
                    if (pickedUri != null) {
                        // overwrite file name with supplier id
                        newPath = saveImageToInternal(pickedUri, "supplier_" + supplier.getId());
                    }

                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("code", code);
                    map.put("phone", phone);
                    map.put("address", address);
                    map.put("description", desc);
                    map.put("image", newPath == null ? "" : newPath);

                    db.collection("suppliers").document(supplier.getId())
                            .update(map)
                            .addOnSuccessListener(aVoid -> {
                                progress.dismiss();
                                setResult(RESULT_OK);
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Supplier updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                            }).addOnFailureListener(e -> {
                                progress.dismiss();
                                runOnUiThread(() -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
