package com.example.foodapp_java.page.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foodapp_java.R;
import com.example.foodapp_java.page.fragment.user.UserFavoriteFragment;
import com.example.foodapp_java.page.fragment.user.UserHomeFragment;
import com.example.foodapp_java.page.fragment.user.UserProfileFragment;
import com.example.foodapp_java.page.fragment.user.UserTransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserFragmentActivity extends AppCompatActivity {

    private Fragment activeFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_fragment);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationUser);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_user_home) {
                selectedFragment = new UserHomeFragment();
            } else if (id == R.id.nav_user_transaction) {
                selectedFragment = new UserTransactionFragment();
            } else if (id == R.id.nav_user_favorite) {
                selectedFragment = new UserFavoriteFragment();
            } else if (id == R.id.nav_user_profile) {
                selectedFragment = new UserProfileFragment();
            }
//            if (selectedFragment != null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container_user, selectedFragment)
//                        .commit();
//            }
//            return true;
//        });
//
//        bottomNav.setSelectedItemId(R.id.nav_user_home);

            // ⛔ Jangan replace jika fragment yang sama diklik lagi
            if (activeFragment != null &&
                    selectedFragment.getClass().equals(activeFragment.getClass())) {
                return true; // IGNORE
            }

            // Ganti fragment hanya jika berbeda
            activeFragment = selectedFragment;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_user, selectedFragment)
                    .commit();

            return true;
        });

        // default
        activeFragment = new UserHomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_user, activeFragment)
                .commit();
    }
}
