package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

        if (validateInput(name, email, password)) {
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null &&
                                !Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty()) {
                            showToast("El correo ya está registrado. Inicia sesión.");
                        } else {
                            createUser(name, email, password);
                        }
                    });
        }
    }

    private boolean validateInput(String name, String email, String password) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showToast("Por favor, completa todos los campos");
            return false;
        }

        if (name.length() < 3) {
            showToast("El nombre debe tener al menos 3 caracteres");
            return false;
        }

        if (password.length() < 6) {
            showToast("La contraseña debe tener al menos 6 caracteres");
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
                                    .addOnSuccessListener(aVoid -> showToast("Registro exitoso. Verifica tu email."))
                                    .addOnFailureListener(e -> showToast("Error al enviar el correo de verificación: " + e.getMessage()));
                        }
                    } else {
                        showToast("Error en el registro: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private void saveUserToFirestore(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", name);
        user.put("email", email);

        db.collection("usuarios").document(userId).set(user)
                .addOnSuccessListener(aVoid -> showToast("Usuario guardado en Firestore"))
                .addOnFailureListener(e -> showToast("Error al guardar usuario: " + e.getMessage()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
