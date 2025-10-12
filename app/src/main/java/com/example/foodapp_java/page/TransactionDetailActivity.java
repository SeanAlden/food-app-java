package com.example.foodapp_java.page;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.TransactionAdapter;
import com.example.foodapp_java.dataBaseOffline.TransactionOffline;
import com.example.foodapp_java.dataBaseOffline.TransactionOfflineDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionDetailActivity extends AppCompatActivity {

    private RecyclerView rvDetail;
    private TextView tvTotal, tvDate, tvTrxId;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        rvDetail = findViewById(R.id.rvTransactionDetail);
        tvTrxId = findViewById(R.id.tvTrxId);
        tvTotal = findViewById(R.id.tvTransactionTotal);
        tvDate = findViewById(R.id.tvTransactionDate);

        int trxId = getIntent().getIntExtra("transactionId", -1);
        if (trxId == -1) {
            finish();
            return;
        }

        TransactionOffline trx = TransactionOfflineDatabase.getInstance(this)
                .transactionDao()
                .getById(trxId);

        if (trx == null) {
            finish();
            return;
        }

        tvTrxId.setText("ID: " + trx.getId());
        tvTotal.setText("Total: Rp " + (long) trx.getTotalPrice());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        tvDate.setText("Date: " + sdf.format(trx.getTimestamp()));

        rvDetail.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this, trx.getItems());
        rvDetail.setAdapter(adapter);
    }
}
