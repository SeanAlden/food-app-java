//package com.example.foodapp_java.page;
//
//import android.os.Bundle;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.adapter.TransactionAdapter;
//import com.example.foodapp_java.dataBaseOffline.TransactionOffline;
//import com.example.foodapp_java.dataBaseOffline.TransactionOfflineDatabase;
//
//import java.text.SimpleDateFormat;
//import java.util.Locale;
//
//public class TransactionDetailActivity extends AppCompatActivity {
//
//    private RecyclerView rvDetail;
//    private TextView tvTotal, tvDate, tvTrxId;
//    private TransactionAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_transaction_detail);
//
//        rvDetail = findViewById(R.id.rvTransactionDetail);
//        tvTrxId = findViewById(R.id.tvTrxId);
//        tvTotal = findViewById(R.id.tvTransactionTotal);
//        tvDate = findViewById(R.id.tvTransactionDate);
//
//        int trxId = getIntent().getIntExtra("transactionId", -1);
//        if (trxId == -1) {
//            finish();
//            return;
//        }
//
//        TransactionOffline trx = TransactionOfflineDatabase.getInstance(this)
//                .transactionDao()
//                .getById(trxId);
//
//        if (trx == null) {
//            finish();
//            return;
//        }
//
//        tvTrxId.setText("ID: " + trx.getId());
//        tvTotal.setText("Total: Rp " + (long) trx.getTotalPrice());
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
//        tvDate.setText("Date: " + sdf.format(trx.getTimestamp()));
//
//        rvDetail.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new TransactionAdapter(this, trx.getItems());
//        rvDetail.setAdapter(adapter);
//    }
//}

package com.example.foodapp_java.page;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.TransactionAdapter;
import com.example.foodapp_java.dataBaseOffline.TransactionOffline;
import com.example.foodapp_java.dataBaseOffline.TransactionOfflineDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionDetailActivity extends AppCompatActivity {

    private RecyclerView rvDetail;
    private TextView tvTotal, tvDate, tvTrxId, tvUserEmail;
    private TransactionAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        rvDetail = findViewById(R.id.rvTransactionDetail);
        tvTrxId = findViewById(R.id.tvTrxId);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvTotal = findViewById(R.id.tvTransactionTotal);
        tvDate = findViewById(R.id.tvTransactionDate);
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbarDetailTransaction);
        toolbar.setTitle("");

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

        // Set basic transaction info
        tvTrxId.setText("ID: " + trx.getId());
        tvTotal.setText("Total: Rp " + (long) trx.getTotalPrice());

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        tvDate.setText("Date: " + sdf.format(trx.getTimestamp()));

        // 🔹 Load user email from Firestore
        if (trx.getUserId() != null && !trx.getUserId().isEmpty()) {
            db.collection("users")
                    .document(trx.getUserId())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String email = doc.getString("email");
                            if (email != null && !email.isEmpty()) {
                                tvUserEmail.setText("Email: " + email);
                            } else {
                                tvUserEmail.setText("Email: (unknown)");
                            }
                        } else {
                            tvUserEmail.setText("Email: (not found)");
                        }
                    })
                    .addOnFailureListener(e -> tvUserEmail.setText("Email: (error)"));
        } else {
            tvUserEmail.setText("Email: (unknown)");
        }

        // Recycler setup
        rvDetail.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(this, trx.getItems());
        rvDetail.setAdapter(adapter);
    }
}
