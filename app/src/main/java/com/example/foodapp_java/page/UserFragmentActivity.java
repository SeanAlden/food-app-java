package com.example.foodapp_java.page;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.foodapp_java.R;
import com.example.foodapp_java.page.fragment.user.UserChatFragment;
import com.example.foodapp_java.page.fragment.user.UserHomeFragment;
import com.example.foodapp_java.page.fragment.user.UserProfileFragment;
import com.example.foodapp_java.page.fragment.user.UserTransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserFragmentActivity extends AppCompatActivity {
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
            } else if (id == R.id.nav_user_chat) {
                selectedFragment = new UserChatFragment();
            } else if (id == R.id.nav_user_profile) {
                selectedFragment = new UserProfileFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_user, selectedFragment)
                        .commit();
            }
            return true;
        });

        bottomNav.setSelectedItemId(R.id.nav_user_home);
    }
}
