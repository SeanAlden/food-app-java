package com.example.foodapp_java.page.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.foodapp_java.R;

public class AdminTransactionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_transaction, container, false);
        // Setup Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbarAdminTransaction);
        toolbar.setTitle("");
        return view;
    }
}
