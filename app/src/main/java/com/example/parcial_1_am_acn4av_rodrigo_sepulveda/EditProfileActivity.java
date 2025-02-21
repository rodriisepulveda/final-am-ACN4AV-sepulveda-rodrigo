package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Button buttonSaveProfile, buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        // Referencias UI
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);
        buttonCancel = findViewById(R.id.buttonCancel);

        // Cargar datos actuales del usuario
        loadUserData();

        // Guardar cambios en Firestore
        buttonSaveProfile.setOnClickListener(v -> updateProfile());

        // Cancelar edición y volver
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadUserData() {
        if (user != null) {
            db.collection("usuarios").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            editTextName.setText(documentSnapshot.getString("nombre"));
                            editTextEmail.setText(documentSnapshot.getString("email"));
                        }
                    })
                    .addOnFailureListener(e -> showToast(getString(R.string.toast_error_loading_data)));
        }
    }

    private void updateProfile() {
        String newName = editTextName.getText().toString().trim();
        String newEmail = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newEmail)) {
            showToast(getString(R.string.toast_fill_all_fields));
            return;
        }

        DocumentReference userRef = db.collection("usuarios").document(user.getUid());
        userRef.update("nombre", newName, "email", newEmail)
                .addOnSuccessListener(aVoid -> {
                    showToast(getString(R.string.toast_profile_updated));
                    finish();
                })
                .addOnFailureListener(e -> showToast(getString(R.string.toast_error_updating_profile)));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
