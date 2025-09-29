package com.example.foodapp_java.page;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp_java.R;
import com.example.foodapp_java.dataClass.Supplier;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;

public class DetailSupplierActivity extends AppCompatActivity {
    private Supplier supplier;
    private ImageView iv;
    private TextView tvName, tvCode, tvPhone, tvAddress, tvDesc;
    private Button btnEdit, btnDelete;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_supplier);

        supplier = getIntent().getParcelableExtra("supplier");
        db = FirebaseFirestore.getInstance();

        iv = findViewById(R.id.ivSupplierDetail);
        tvName = findViewById(R.id.tvSupplierDetailName);
        tvCode = findViewById(R.id.tvSupplierDetailCode);
        tvPhone = findViewById(R.id.tvSupplierDetailPhone);
        tvAddress = findViewById(R.id.tvSupplierDetailAddress);
        tvDesc = findViewById(R.id.tvSupplierDetailDesc);
        btnEdit = findViewById(R.id.btnSupplierEdit);
        btnDelete = findViewById(R.id.btnSupplierDelete);

        if (supplier != null) {
            tvName.setText(supplier.getName());
            tvCode.setText(supplier.getCode());
            tvPhone.setText(supplier.getPhone());
            tvAddress.setText(supplier.getAddress());
            tvDesc.setText(supplier.getDescription());

            String img = supplier.getImage();
            if (img != null && !img.isEmpty()) {
                File f = new File(img);
                if (f.exists()) iv.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
                else iv.setImageResource(R.drawable.supplier);
            } else iv.setImageResource(R.drawable.supplier);
        }

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, EditSupplierActivity.class);
            i.putExtra("supplier", supplier);
            startActivityForResult(i, 1234);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete supplier")
                    .setMessage("Are you sure to delete this supplier?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        db.collection("suppliers").document(supplier.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // try delete image file locally
                                    String img = supplier.getImage();
                                    if (img != null && !img.isEmpty()) {
                                        File f = new File(img);
                                        if (f.exists()) f.delete();
                                    }
                                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if edit returned OK, refresh details by re-fetching doc (simpler: close and let list refresh)
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            // close to force parent to refresh via snapshot listener
            setResult(RESULT_OK);
            finish();
        }
    }
}
