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
//import com.example.foodapp_java.page.activity.LoginActivity;
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

//package com.example.foodapp_java.page.fragment.admin;
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
//import com.example.foodapp_java.page.activity.LoginActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//
//public class AdminProfileFragment extends Fragment {
//
//    private FirebaseAuth auth;
//    private FirebaseFirestore db;
//    private FirebaseUser user;
//
//    private ImageView ivAdminProfile;
//    private Button btnChangeAdminPhoto;
//    private Button btnLogout;
//    private TextView tvAdminHello;
//
//    private ActivityResultLauncher<Intent> pickImageLauncher;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);
//        Toolbar toolbar = view.findViewById(R.id.toolbarAdminProfile);
//        toolbar.setTitle("");
//
//        ivAdminProfile = view.findViewById(R.id.ivAdminProfile);
//        btnChangeAdminPhoto = view.findViewById(R.id.btnChangeAdminPhoto);
//        btnLogout = view.findViewById(R.id.btnLogout);
//        tvAdminHello = view.findViewById(R.id.tvAdminHello);
//
//        auth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        user = auth.getCurrentUser();
//
//        // ActivityResult untuk pick gambar dari gallery
//        pickImageLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
//                        Uri uri = result.getData().getData();
//                        if (uri != null) {
//                            saveImageToLocalAndUpload(uri);
//                        }
//                    }
//                }
//        );
//
//        // load current admin data
//        if (user != null) {
//            db.collection("users").document(user.getUid())
//                    .get()
//                    .addOnSuccessListener(doc -> {
//                        if (doc.exists()) {
//                            String name = doc.getString("name");
//                            tvAdminHello.setText("Hi, " + (name != null && !name.isEmpty() ? name : user.getEmail()));
//
//                            String profileUrl = doc.getString("profileUrl");
//                            loadProfileImage(profileUrl);
//                        } else {
//                            tvAdminHello.setText("Hi, " + (user.getEmail() != null ? user.getEmail() : "Admin"));
//                            loadProfileImage(null);
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        tvAdminHello.setText("Hi, " + (user != null && user.getEmail() != null ? user.getEmail() : "Admin"));
//                        loadProfileImage(null);
//                    });
//        }
//
//        btnChangeAdminPhoto.setOnClickListener(v -> openGallery());
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
//    private void saveImageToLocalAndUpload(Uri uri) {
//        try {
//            File dir = new File(requireContext().getFilesDir(), "profile_images");
//            if (!dir.exists()) dir.mkdirs();
//
//            File out = new File(dir, (user != null ? user.getUid() : "admin") + "_profile.jpg");
//
//            try (InputStream is = requireContext().getContentResolver().openInputStream(uri);
//                 FileOutputStream fos = new FileOutputStream(out)) {
//                byte[] buf = new byte[1024];
//                int len;
//                while ((len = is.read(buf)) > 0) {
//                    fos.write(buf, 0, len);
//                }
//            }
//
//            String localPath = out.getAbsolutePath();
//
//            // update Firestore users.profileUrl
//            if (user != null) {
//                db.collection("users").document(user.getUid())
//                        .update("profileUrl", localPath)
//                        .addOnSuccessListener(aVoid -> {
//                            loadProfileImage(localPath);
//                            Toast.makeText(getContext(), "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(getContext(), "Gagal menyimpan path ke Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        });
//            } else {
//                // fallback load local (walaupun tidak disimpan di Firestore)
//                loadProfileImage(localPath);
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            Toast.makeText(getContext(), "Gagal menyimpan gambar: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void loadProfileImage(String path) {
//        if (path != null) {
//            File f = new File(path);
//            if (f.exists()) {
//                Glide.with(this).load(f).circleCrop().into(ivAdminProfile);
//                return;
//            }
//        }
//        // default fallback
//        Glide.with(this).load(R.drawable.profile).circleCrop().into(ivAdminProfile);
//    }
//}

package com.example.foodapp_java.page.fragment.admin;

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
import android.widget.LinearLayout;
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
import com.example.foodapp_java.page.activity.LoginActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AdminProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private ImageView ivAdminProfile;
    private Button btnChangeAdminPhoto, btnLogout;
    private LinearLayout btnEditProfile, btnChangePassword, btnChangeAddress;
    private TextView tvAdminHello, tvAdminEmail, tvAdminPhone, tvAdminAddress;

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
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangeAddress = view.findViewById(R.id.btnChangeAddress);

        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        btnChangeAddress.setOnClickListener(v -> showChangeAddressDialog());
        tvAdminHello = view.findViewById(R.id.tvAdminHello);
        tvAdminEmail = view.findViewById(R.id.tvAdminEmail);
        tvAdminPhone = view.findViewById(R.id.tvAdminPhone);
        tvAdminAddress = view.findViewById(R.id.tvAdminAddress);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) saveImageToLocalAndUpload(uri);
                    }
                }
        );

        loadAdminData();

        btnChangeAdminPhoto.setOnClickListener(v -> openGallery());
        btnEditProfile.setOnClickListener(v -> showEditDialog());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void loadAdminData() {
        if (user == null) return;
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String phone = doc.getString("phone");
                        String profileUrl = doc.getString("profileUrl");
                        String address = doc.getString("address");

                        tvAdminHello.setText("Hi, " + (name != null && !name.isEmpty() ? name : "Admin"));
                        tvAdminEmail.setText(email != null ? email : "-");
                        tvAdminPhone.setText(phone != null ? phone : "-");
                        tvAdminAddress.setText(address != null ? address : "-");

                        loadProfileImage(profileUrl);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal memuat data admin", Toast.LENGTH_SHORT).show());
    }

    private void showChangeAddressDialog() {
        EditText et = new EditText(getContext());
        et.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);
        et.setHint("Enter new address");

        new AlertDialog.Builder(getContext())
                .setTitle("Change Address")
                .setView(et)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newAddress = et.getText().toString().trim();
                    if (newAddress.isEmpty()) {
                        Toast.makeText(getContext(), "Address cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("address", newAddress);
                    db.collection("users").document(user.getUid())
                            .update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Address updated", Toast.LENGTH_SHORT).show();
                                loadAdminData();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update address", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showChangePasswordDialog() {
        // create vertical layout with 3 EditTexts
        android.widget.LinearLayout container = new android.widget.LinearLayout(getContext());
        container.setOrientation(android.widget.LinearLayout.VERTICAL);
        int pad = (int) (8 * getResources().getDisplayMetrics().density);
        container.setPadding(pad, pad, pad, pad);

        EditText etCurrent = new EditText(getContext());
        etCurrent.setHint("Current password");
        etCurrent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(etCurrent);

        EditText etNew = new EditText(getContext());
        etNew.setHint("New password (min 6)");
        etNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(etNew);

        EditText etConfirm = new EditText(getContext());
        etConfirm.setHint("Confirm new password");
        etConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        container.addView(etConfirm);

        new AlertDialog.Builder(getContext())
                .setTitle("Change Password")
                .setView(container)
                .setPositiveButton("Change", (dialog, which) -> {
                    String current = etCurrent.getText().toString();
                    String neu = etNew.getText().toString();
                    String conf = etConfirm.getText().toString();

                    if (current.isEmpty() || neu.isEmpty() || conf.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!neu.equals(conf)) {
                        Toast.makeText(getContext(), "New password and confirm do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (neu.length() < 6) {
                        Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Reauthenticate then update password
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), current);
                    user.reauthenticate(credential)
                            .addOnSuccessListener(aVoid -> {
                                user.updatePassword(neu)
                                        .addOnSuccessListener(aVoid2 -> Toast.makeText(getContext(), "Password changed", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to change password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Current password incorrect", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
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

    private void saveImageToLocalAndUpload(Uri uri) {
        try {
            File dir = new File(requireContext().getFilesDir(), "profile_images");
            if (!dir.exists()) dir.mkdirs();

            File out = new File(dir, (user != null ? user.getUid() : "admin") + "_profile.jpg");

            try (InputStream is = requireContext().getContentResolver().openInputStream(uri);
                 FileOutputStream fos = new FileOutputStream(out)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
            }

            String localPath = out.getAbsolutePath();
            db.collection("users").document(user.getUid())
                    .update("profileUrl", localPath)
                    .addOnSuccessListener(aVoid -> {
                        loadProfileImage(localPath);
                        Toast.makeText(getContext(), "Foto profil diperbarui", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal menyimpan foto", Toast.LENGTH_SHORT).show());

        } catch (Exception ex) {
            Toast.makeText(getContext(), "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
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
        Glide.with(this).load(R.drawable.profile).circleCrop().into(ivAdminProfile);
    }

    private void showEditDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        EditText etName = dialogView.findViewById(R.id.etEditName);
        EditText etEmail = dialogView.findViewById(R.id.etEditEmail);
        EditText etPhone = dialogView.findViewById(R.id.etEditPhone);

        etEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        etPhone.setInputType(InputType.TYPE_CLASS_PHONE);

        etName.setText(tvAdminHello.getText().toString().replace("Hi, ", ""));
        etEmail.setText(tvAdminEmail.getText().toString());
        etPhone.setText(tvAdminPhone.getText().toString());

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Profil Admin")
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
                    loadAdminData();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal update: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}

