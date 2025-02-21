package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencias UI
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        Button buttonBackToLogin = findViewById(R.id.buttonBackToLogin);

        // Acción del botón "Registrarse"
        buttonRegister.setOnClickListener(v -> registerUser());

        // Volver a la pantalla de Login
        buttonBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!validateInput(name, email, password)) return;

        // Verifica si el email ya está registrado
        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null &&
                            !Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty()) {
                        showAlert("El correo ya está registrado. Inicia sesión.", SweetAlertDialog.WARNING_TYPE);
                    } else {
                        createUser(name, email, password);
                    }
                })
                .addOnFailureListener(e -> showAlert("Error al verificar email: " + e.getMessage(), SweetAlertDialog.ERROR_TYPE));
    }

    private boolean validateInput(String name, String email, String password) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showAlert("Por favor, completa todos los campos", SweetAlertDialog.WARNING_TYPE);
            return false;
        }

        if (name.length() < 3) {
            showAlert("El nombre debe tener al menos 3 caracteres", SweetAlertDialog.WARNING_TYPE);
            return false;
        }

        if (password.length() < 6) {
            showAlert("La contraseña debe tener al menos 6 caracteres", SweetAlertDialog.WARNING_TYPE);
            return false;
        }

        return true;
    }

    private void createUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), name, email);
                            user.sendEmailVerification()
                                    .addOnSuccessListener(aVoid -> showAlert("Registro exitoso. Verifica tu email.", SweetAlertDialog.SUCCESS_TYPE))
                                    .addOnFailureListener(e -> showAlert("Error al enviar email de verificación: " + e.getMessage(), SweetAlertDialog.ERROR_TYPE));
                        }
                    } else {
                        showAlert("Error en el registro: " + Objects.requireNonNull(task.getException()).getMessage(), SweetAlertDialog.ERROR_TYPE);
                    }
                })
                .addOnFailureListener(e -> showAlert("Error en Firebase Auth: " + e.getMessage(), SweetAlertDialog.ERROR_TYPE));
    }

    private void saveUserToFirestore(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", name);
        user.put("email", email);

        db.collection("usuarios").document(userId).set(user)
                .addOnSuccessListener(aVoid -> showAlert("Usuario guardado en Firestore correctamente", SweetAlertDialog.SUCCESS_TYPE))
                .addOnFailureListener(e -> showAlert("Error al guardar usuario en Firestore: " + e.getMessage(), SweetAlertDialog.ERROR_TYPE));
    }

    private void showAlert(String message, int type) {
        new SweetAlertDialog(this, type)
                .setTitleText(message)
                .show();
    }
}
