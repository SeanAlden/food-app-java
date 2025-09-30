//package com.example.firebaseauth;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//
//public class MainActivity extends AppCompatActivity {
//
//    FirebaseAuth auth;
//    Button button;
//    TextView textView;
//    FirebaseUser user;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        auth = FirebaseAuth.getInstance();
//        button = findViewById(R.id.logout);
//        textView = findViewById(R.id.user_details);
//        user = auth.getCurrentUser();
//        if (user == null){
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//        }
//        else {
//            textView.setText(user.getEmail());
//        }
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }
//}

//package com.example.foodapp_java;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.example.foodapp_java.page.AdminHomeActivity;
//import com.example.foodapp_java.page.HomeActivity;
//import com.example.foodapp_java.page.LoginActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class MainActivity extends AppCompatActivity {
//
//    FirebaseAuth auth;
//    FirebaseUser user;
//    FirebaseFirestore db;
//
//    @SuppressLint("MissingInflatedId")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        user = auth.getCurrentUser();
//
//        if (user == null) {
//            // Jika user belum login → arahkan ke Login
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            // Jika user sudah login → cek usertype di Firestore
//            db.collection("users").document(user.getUid())
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                String userType = document.getString("usertype");
//                                if ("user".equals(userType)) {
//                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    Intent intent = new Intent(getApplicationContext(), AdminHomeActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            } else {
//                                // Jika data user tidak ada di Firestore
//                                Toast.makeText(MainActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
//                                auth.signOut();
//                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        } else {
//                            Toast.makeText(MainActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
//                            auth.signOut();
//                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    });
//        }
//    }
//}

package com.example.foodapp_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.foodapp_java.page.AdminFragmentActivity;
import com.example.foodapp_java.page.LoginActivity;
import com.example.foodapp_java.page.UserFragmentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseFirestore db;
    private ProgressBar progressBar; // optional: spinner di layout splash

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // gunakan layout splash sederhana berisi progressbar (lihat contoh dibawah)
        setContentView(R.layout.activity_main);

//        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Listener yang menunggu Firebase restore auth state
        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            Log.d(TAG, "AuthState changed: user = " + (user != null ? user.getUid() : "null"));

            if (user == null) {
                // user benar-benar belum login -> kirim ke LoginActivity
                // lakukan sedikit delay UI jika mau smoothing
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                // user ada -> ambil dokumen Firestore untuk cek usertype
                db.collection("users").document(user.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> handleUserDocument(documentSnapshot))
                        .addOnFailureListener(e -> {
                            // Jangan otomatis signOut jika gagal karena network sementara.
                            // Tampilkan pesan dan coba sekali retry, atau arahkan user ke halaman utama
                            // sesuai kebijakan Anda. Di sini saya tampilkan toast dan coba lagi 2 detik:
                            Log.w(TAG, "Failed to fetch user doc", e);
                            Toast.makeText(MainActivity.this, "Network error, mencoba kembali...", Toast.LENGTH_SHORT).show();
                            // simple retry (bisa diganti exponential backoff)
                            db.collection("users").document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(MainActivity.this::handleUserDocument)
                                    .addOnFailureListener(e2 -> {
                                        // setelah retry gagal: aman-nya arahkan ke Login (atau fallback)
                                        Log.e(TAG, "Retry failed, signing out", e2);
                                        auth.signOut();
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                        finish();
                                    });
                        });
            }
        };
    }

//    private void handleUserDocument(DocumentSnapshot document) {
//        if (document != null && document.exists()) {
//            String userType = document.getString("usertype");
//            Log.d(TAG, "userType = " + userType);
//
//            if ("user".equals(userType)) {
//                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            } else {
//                Intent intent = new Intent(MainActivity.this, AdminHomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }
//        } else {
//            // dokumen tidak ada -> kemungkinan akun belum lengkap => logout dan arahkan ke login/register
//            Toast.makeText(this, "User data not found. Please login again.", Toast.LENGTH_SHORT).show();
//            auth.signOut();
//            startActivity(new Intent(MainActivity.this, LoginActivity.class)
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
//            finish();
//        }
//    }

    private void handleUserDocument(DocumentSnapshot document) {
        if (document != null && document.exists()) {
            String userType = document.getString("usertype");
            Log.d(TAG, "userType = " + userType);

            if ("user".equals(userType)) {
                Intent intent = new Intent(MainActivity.this, UserFragmentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, AdminFragmentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, "User data not found. Please login again.", Toast.LENGTH_SHORT).show();
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener); // mulai mendengarkan
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) auth.removeAuthStateListener(authListener);
    }
}
