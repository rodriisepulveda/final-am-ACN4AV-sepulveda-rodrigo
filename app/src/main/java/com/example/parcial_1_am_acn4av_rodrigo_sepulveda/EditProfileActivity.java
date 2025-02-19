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

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_edit_profile);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

        // Referencias UI
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        Button buttonSaveProfile = findViewById(R.id.buttonSaveProfile);
        Button buttonCancel = findViewById(R.id.buttonCancel);

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
                    .addOnFailureListener(e -> showToast("Error al cargar datos"));
        }
    }

    private void updateProfile() {
        String newName = editTextName.getText().toString().trim();
        String newEmail = editTextEmail.getText().toString().trim();

        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newEmail)) {
            showToast("Por favor, completa todos los campos");
            return;
        }

        DocumentReference userRef = db.collection("usuarios").document(user.getUid());
        userRef.update("nombre", newName, "email", newEmail)
                .addOnSuccessListener(aVoid -> {
                    showToast("Perfil actualizado correctamente");
                    finish();
                })
                .addOnFailureListener(e -> showToast("Error al actualizar perfil"));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
