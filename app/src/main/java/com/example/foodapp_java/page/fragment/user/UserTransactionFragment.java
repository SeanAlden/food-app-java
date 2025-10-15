//package com.example.foodapp_java.page.fragment.user;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.adapter.TransactionListAdapter;
//import com.example.foodapp_java.dataBaseOffline.TransactionOffline;
//import com.example.foodapp_java.dataBaseOffline.TransactionOfflineDatabase;
//
//import java.util.List;
//
//public class UserTransactionFragment extends Fragment {
//    private RecyclerView rvTransactions;
//    private TransactionListAdapter adapter;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_user_transaction, container, false);
//        Toolbar toolbar = view.findViewById(R.id.toolbarUserTransaction);
//        toolbar.setTitle("");
//
//        rvTransactions = view.findViewById(R.id.rvTransactions);
//        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        loadTransactions();
//
//        return view;
//    }
//
//    private void loadTransactions() {
//        try {
//            TransactionOfflineDatabase dbRoom = TransactionOfflineDatabase.getInstance(requireContext());
//            List<TransactionOffline> transactions = dbRoom.transactionDao().getAll();
//
//            if (transactions == null || transactions.isEmpty()) {
//                // show empty state (optional) - for now do a Toast
//                // Toast.makeText(getContext(), "No transactions yet", Toast.LENGTH_SHORT).show();
//            }
//
//            adapter = new TransactionListAdapter(getContext(), transactions);
//            rvTransactions.setAdapter(adapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getContext(), "Failed to load transactions", Toast.LENGTH_SHORT).show();
//        }
//    }
//}

package com.example.foodapp_java.page.fragment.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodapp_java.R;
import com.example.foodapp_java.adapter.TransactionListAdapter;
import com.example.foodapp_java.dataBaseOffline.TransactionOffline;
import com.example.foodapp_java.dataBaseOffline.TransactionOfflineDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class UserTransactionFragment extends Fragment {
    private RecyclerView rvTransactions;
    private TransactionListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_transaction, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbarUserTransaction);
        toolbar.setTitle("");

        rvTransactions = view.findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTransactions();

        return view;
    }

    private void loadTransactions() {
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        if (current == null) {
            Toast.makeText(getContext(), "Please login to see your transactions", Toast.LENGTH_SHORT).show();
            return;
        }
        String uid = current.getUid();

        try {
            TransactionOfflineDatabase dbRoom = TransactionOfflineDatabase.getInstance(requireContext());
            List<TransactionOffline> all = dbRoom.transactionDao().getAll();

            // Filter hanya transaksi milik user saat ini
            List<TransactionOffline> mine = new ArrayList<>();
            if (all != null && !all.isEmpty()) {
                for (TransactionOffline t : all) {
                    if (t == null) continue;
                    String tUserId = t.getUserId();
                    if (tUserId != null && tUserId.equals(uid)) {
                        mine.add(t);
                    }
                }
            }

            if (mine.isEmpty()) {
                // optional: tampilkan state kosong, mis. Toast atau view kosong
                // Toast.makeText(getContext(), "No transactions yet", Toast.LENGTH_SHORT).show();
            }

            adapter = new TransactionListAdapter(getContext(), mine);
            rvTransactions.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to load transactions", Toast.LENGTH_SHORT).show();
        }
    }
}
