package com.example.foodapp_java.page.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foodapp_java.R;
import com.example.foodapp_java.page.fragment.admin.AdminHomeFragment;
import com.example.foodapp_java.page.fragment.admin.AdminProfileFragment;
import com.example.foodapp_java.page.fragment.admin.AdminTransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

//public class AdminFragmentActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_admin_fragment);
//
//        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationAdmin);
//        bottomNav.setOnItemSelectedListener(item -> {
//            Fragment selectedFragment = null;
//            int id = item.getItemId();
//            if (id == R.id.nav_admin_home) {
//                selectedFragment = new AdminHomeFragment();
//            } else if (id == R.id.nav_admin_transaction) {
//                selectedFragment = new AdminTransactionFragment();
//            } else if (id == R.id.nav_admin_profile) {
//                selectedFragment = new AdminProfileFragment();
//            }
//            if (selectedFragment != null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container_admin, selectedFragment)
//                        .commit();
//            }
//            return true;
//        });
//
//        // default fragment
//        bottomNav.setSelectedItemId(R.id.nav_admin_home);
//    }
//}

public class AdminFragmentActivity extends AppCompatActivity {

    private Fragment activeFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_fragment);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationAdmin);

        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_admin_home) {
                selectedFragment = new AdminHomeFragment();
            } else if (id == R.id.nav_admin_transaction) {
                selectedFragment = new AdminTransactionFragment();
            } else if (id == R.id.nav_admin_profile) {
                selectedFragment = new AdminProfileFragment();
            }

            // ⛔ Jangan replace jika fragment yang sama diklik lagi
            if (activeFragment != null &&
                    selectedFragment.getClass().equals(activeFragment.getClass())) {
                return true; // IGNORE
            }

            // Ganti fragment hanya jika berbeda
            activeFragment = selectedFragment;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_admin, selectedFragment)
                    .commit();

            return true;
        });

        // default
        activeFragment = new AdminHomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_admin, activeFragment)
                .commit();
    }
}

