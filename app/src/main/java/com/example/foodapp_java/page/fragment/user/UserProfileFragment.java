//package com.example.foodapp_java.page.fragment.user;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.page.HomeActivity;
//import com.example.foodapp_java.page.LoginActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class UserProfileFragment extends Fragment {
//
//    private FirebaseAuth auth;
//    private FirebaseFirestore db;
//    private FirebaseUser user;
//
//    // Ambil TextView
//    private TextView tvHello;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
//        Toolbar toolbar = view.findViewById(R.id.toolbarUserProfile);
//        toolbar.setTitle("");
//
//        // Ambil data user dari intent
////        User user = getIntent().getParcelableExtra("user");
////
////        if (user != null && user.getName() != null){
////            tvHello.setText("Hello, " + user.getName());
////        } else {
////            tvHello.setText("Hello, Guest");
////        }
//
//        // Firebase init
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        user = auth.getCurrentUser();
//
//        // Header
//        tvHello = view.findViewById(R.id.tvHello);
//
//        if (user != null) {
//            db.collection("users").document(user.getUid())
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            String name = documentSnapshot.getString("name");
//                            if (name != null && !name.isEmpty()) {
//                                tvHello.setText("Hi, " + name);
//                            } else {
//                                tvHello.setText("Hi, " + user.getEmail());
//                            }
//                        } else {
//                            tvHello.setText("Hi, " + user.getEmail());
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
//                        tvHello.setText("Hi, " + user.getEmail());
//                    });
//        }
//
//        // Tombol logout
//        Button btnLogout = view.findViewById(R.id.btnLogout);
//        btnLogout.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(getActivity(), LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            requireActivity().finish();
//        });
//
//        return view;
//    }
//}

//package com.example.foodapp_java.page.fragment.user;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//
//import com.bumptech.glide.Glide;
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.page.LoginActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//
//public class UserProfileFragment extends Fragment {
//
//    private FirebaseAuth auth;
//    private FirebaseFirestore db;
//    private FirebaseUser user;
//
//    private TextView tvHello;
//    private ImageView ivProfileUser;
//    private Button btnLogout, btnChangePhoto;
//
//    private ActivityResultLauncher<Intent> pickImageLauncher;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
//        Toolbar toolbar = view.findViewById(R.id.toolbarUserProfile);
//        toolbar.setTitle("");
//
//        // Firebase init
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        user = auth.getCurrentUser();
//
//        tvHello = view.findViewById(R.id.tvHello);
//        ivProfileUser = view.findViewById(R.id.ivProfileUser);
//        btnLogout = view.findViewById(R.id.btnLogout);
//        btnChangePhoto = view.findViewById(R.id.btnChangePhoto);
//
//        // ActivityResultLauncher untuk pilih gambar
//        pickImageLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
//                        Uri uri = result.getData().getData();
//                        if (uri != null) {
//                            saveImageToLocal(uri);
//                        }
//                    }
//                }
//        );
//
//        // Load data user
//        if (user != null) {
//            db.collection("users").document(user.getUid())
//                    .get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (documentSnapshot.exists()) {
//                            String name = documentSnapshot.getString("name");
//                            tvHello.setText("Hi, " + (name != null && !name.isEmpty() ? name : user.getEmail()));
//
//                            String profileUrl = documentSnapshot.getString("profileUrl");
//                            loadProfileImage(profileUrl);
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
//                        tvHello.setText("Hi, " + user.getEmail());
//                    });
//        }
//
//        // Tombol ganti foto
//        btnChangePhoto.setOnClickListener(v -> openGallery());
//
//        // Tombol logout
//        btnLogout.setOnClickListener(v -> {
//            FirebaseAuth.getInstance().signOut();
//            Intent intent = new Intent(getActivity(), LoginActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            requireActivity().finish();
//        });
//
//        return view;
//    }
//
//    private void openGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType("image/*");
//        pickImageLauncher.launch(intent);
//    }
//
//    private void saveImageToLocal(Uri uri) {
//        try {
//            File directory = new File(requireContext().getFilesDir(), "profile_images");
//            if (!directory.exists()) directory.mkdirs();
//
//            File file = new File(directory, user.getUid() + "_profile.jpg");
//
//            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
//                 FileOutputStream outputStream = new FileOutputStream(file)) {
//                byte[] buffer = new byte[1024];
//                int len;
//                while ((len = inputStream.read(buffer)) > 0) {
//                    outputStream.write(buffer, 0, len);
//                }
//            }
//
//            String localPath = file.getAbsolutePath();
//
//            // Simpan path ke Firestore
//            db.collection("users").document(user.getUid())
//                    .update("profileUrl", localPath)
//                    .addOnSuccessListener(unused -> {
//                        loadProfileImage(localPath);
//                        Toast.makeText(getContext(), "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "Gagal update foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getContext(), "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void loadProfileImage(String path) {
//        if (path != null) {
//            File file = new File(path);
//            if (file.exists()) {
//                Glide.with(this).load(file).circleCrop().into(ivProfileUser);
//                return;
//            }
//        }
//        // default image
//        Glide.with(this).load(R.drawable.ic_profile).circleCrop().into(ivProfileUser);
//    }
//}

package com.example.foodapp_java.page.fragment.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.foodapp_java.R;
import com.example.foodapp_java.page.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private TextView tvHello, tvEmail, tvPhone;
    private ImageView ivProfileUser;
    private Button btnLogout, btnChangePhoto, btnEditProfile;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbarUserProfile);
        toolbar.setTitle("");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        tvHello = view.findViewById(R.id.tvHello);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        ivProfileUser = view.findViewById(R.id.ivProfileUser);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) saveImageToLocal(uri);
                    }
                }
        );

        loadUserData();

        btnChangePhoto.setOnClickListener(v -> openGallery());
        btnEditProfile.setOnClickListener(v -> showEditDialog());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserData() {
        if (user == null) return;
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String phone = doc.getString("phone");
                        String profileUrl = doc.getString("profileUrl");

                        tvHello.setText("Hi, " + (name != null && !name.isEmpty() ? name : user.getEmail()));
                        tvEmail.setText(email != null ? email : "-");
                        tvPhone.setText(phone != null ? phone : "-");
                        loadProfileImage(profileUrl);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal memuat data user", Toast.LENGTH_SHORT).show());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void saveImageToLocal(Uri uri) {
        try {
            File directory = new File(requireContext().getFilesDir(), "profile_images");
            if (!directory.exists()) directory.mkdirs();

            File file = new File(directory, user.getUid() + "_profile.jpg");

            try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(file)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, len);
            }

            String localPath = file.getAbsolutePath();
            db.collection("users").document(user.getUid())
                    .update("profileUrl", localPath)
                    .addOnSuccessListener(aVoid -> {
                        loadProfileImage(localPath);
                        Toast.makeText(getContext(), "Foto profil diperbarui", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal update foto", Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImage(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                Glide.with(this).load(file).circleCrop().into(ivProfileUser);
                return;
            }
        }
        Glide.with(this).load(R.drawable.ic_profile).circleCrop().into(ivProfileUser);
    }

    private void showEditDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etName = dialogView.findViewById(R.id.etEditName);
        EditText etEmail = dialogView.findViewById(R.id.etEditEmail);
        EditText etPhone = dialogView.findViewById(R.id.etEditPhone);

        etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        etName.setText(tvHello.getText().toString().replace("Hi, ", ""));
        etEmail.setText(tvEmail.getText().toString());
        etPhone.setText(tvPhone.getText().toString());

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Profil")
                .setView(dialogView)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    updateProfile(name, email, phone);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void updateProfile(String name, String email, String phone) {
        if (user == null) return;
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("email", email);
        updates.put("phone", phone);

        db.collection("users").document(user.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profil diperbarui", Toast.LENGTH_SHORT).show();
                    loadUserData();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal update profil", Toast.LENGTH_SHORT).show());
    }
}
