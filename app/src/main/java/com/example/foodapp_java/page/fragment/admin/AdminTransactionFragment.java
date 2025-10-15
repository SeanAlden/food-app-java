package com.example.foodapp_java.page.fragment.admin;

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
import com.example.foodapp_java.adapter.AdminTransactionListAdapter;
import com.example.foodapp_java.adapter.TransactionListAdapter;
import com.example.foodapp_java.dataBaseOffline.TransactionOffline;
import com.example.foodapp_java.dataBaseOffline.TransactionOfflineDatabase;

import java.util.List;

public class AdminTransactionFragment extends Fragment {
    private RecyclerView rvTransactions;
    private AdminTransactionListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_transaction, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbarAdminTransaction);
        toolbar.setTitle("");

        rvTransactions = view.findViewById(R.id.rvAdminTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTransactions();

        return view;
    }

    private void loadTransactions() {
        try {
            TransactionOfflineDatabase dbRoom = TransactionOfflineDatabase.getInstance(requireContext());
            List<TransactionOffline> transactions = dbRoom.transactionDao().getAll();

            if (transactions == null || transactions.isEmpty()) {
                // show empty state (optional) - for now do a Toast
                // Toast.makeText(getContext(), "No transactions yet", Toast.LENGTH_SHORT).show();
            }

            adapter = new AdminTransactionListAdapter(getContext(), transactions);
            rvTransactions.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to load transactions", Toast.LENGTH_SHORT).show();
        }
    }
}
