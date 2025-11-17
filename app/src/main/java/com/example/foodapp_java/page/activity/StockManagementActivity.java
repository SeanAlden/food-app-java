package com.example.foodapp_java.page.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodapp_java.R;

public class StockManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Tombol Entry Stock
        Button btnEntry = findViewById(R.id.btnEntryStock);
        btnEntry.setOnClickListener(v -> {
            Intent intent = new Intent(StockManagementActivity.this, EntryStockActivity.class);
            startActivity(intent);
        });

        // Tombol Exit Stock
        Button btnExit = findViewById(R.id.btnExitStock);
        btnExit.setOnClickListener(v -> {
            Intent intent = new Intent(StockManagementActivity.this, OutgoingStockActivity.class);
            startActivity(intent);
        });
    }
}
