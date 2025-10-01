//package com.example.foodapp_java.page.fragment.admin;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.widget.Toolbar;
//import androidx.fragment.app.Fragment;
//
//import com.example.foodapp_java.R;
//import com.example.foodapp_java.page.LoginActivity;
//import com.google.firebase.auth.FirebaseAuth;
//
//public class AdminProfileFragment extends Fragment {
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);
//        Toolbar toolbar = view.findViewById(R.id.toolbarAdminProfile);
//        toolbar.setTitle("");
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

package com.example.foodapp_java.page.fragment.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class AdminProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private ImageView ivAdminProfile;
    private Button btnChangeAdminPhoto;
    private Button btnLogout;
    private TextView tvAdminHello;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbarAdminProfile);
        toolbar.setTitle("");

        ivAdminProfile = view.findViewById(R.id.ivAdminProfile);
        btnChangeAdminPhoto = view.findViewById(R.id.btnChangeAdminPhoto);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvAdminHello = view.findViewById(R.id.tvAdminHello);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        // ActivityResult untuk pick gambar dari gallery
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            saveImageToLocalAndUpload(uri);
                        }
                    }
                }
        );

        // load current admin data
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String name = doc.getString("name");
                            tvAdminHello.setText("Hi, " + (name != null && !name.isEmpty() ? name : user.getEmail()));

                            String profileUrl = doc.getString("profileUrl");
                            loadProfileImage(profileUrl);
                        } else {
                            tvAdminHello.setText("Hi, " + (user.getEmail() != null ? user.getEmail() : "Admin"));
                            loadProfileImage(null);
                        }
                    })
                    .addOnFailureListener(e -> {
                        tvAdminHello.setText("Hi, " + (user != null && user.getEmail() != null ? user.getEmail() : "Admin"));
                        loadProfileImage(null);
                    });
        }

        btnChangeAdminPhoto.setOnClickListener(v -> openGallery());
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void saveImageToLocalAndUpload(Uri uri) {
        try {
            File dir = new File(requireContext().getFilesDir(), "profile_images");
            if (!dir.exists()) dir.mkdirs();

            File out = new File(dir, (user != null ? user.getUid() : "admin") + "_profile.jpg");

            try (InputStream is = requireContext().getContentResolver().openInputStream(uri);
                 FileOutputStream fos = new FileOutputStream(out)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
            }

            String localPath = out.getAbsolutePath();

            // update Firestore users.profileUrl
            if (user != null) {
                db.collection("users").document(user.getUid())
                        .update("profileUrl", localPath)
                        .addOnSuccessListener(aVoid -> {
                            loadProfileImage(localPath);
                            Toast.makeText(getContext(), "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Gagal menyimpan path ke Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                // fallback load local (walaupun tidak disimpan di Firestore)
                loadProfileImage(localPath);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getContext(), "Gagal menyimpan gambar: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileImage(String path) {
        if (path != null) {
            File f = new File(path);
            if (f.exists()) {
                Glide.with(this).load(f).circleCrop().into(ivAdminProfile);
                return;
            }
        }
        // default fallback
        Glide.with(this).load(R.drawable.profile).circleCrop().into(ivAdminProfile);
    }
}
