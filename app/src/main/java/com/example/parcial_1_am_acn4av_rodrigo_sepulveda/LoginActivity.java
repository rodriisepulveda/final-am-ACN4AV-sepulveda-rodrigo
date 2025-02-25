package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias UI
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonRegister = findViewById(R.id.buttonRegister);
        Button buttonForgotPassword = findViewById(R.id.buttonForgotPassword); // Nuevo botón

        // Botón de Iniciar Sesión
        buttonLogin.setOnClickListener(view -> loginUser());

        // Botón de Registro (redirige a RegisterActivity)
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Botón de Recuperar Contraseña
        buttonForgotPassword.setOnClickListener(v -> resetPassword());
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // Si el usuario ya está autenticado y verificó su email, lo redirige a MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showAlert("Por favor, completa todos los campos", SweetAlertDialog.WARNING_TYPE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            showAlert("Inicio de sesión exitoso", SweetAlertDialog.SUCCESS_TYPE);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            showAlert("Verifica tu correo antes de iniciar sesión", SweetAlertDialog.WARNING_TYPE);
                        }
                    } else {
                        showAlert("Error en el inicio de sesión: " + Objects.requireNonNull(task.getException()).getMessage(),
                                SweetAlertDialog.ERROR_TYPE);
                    }
                });
    }

    private void resetPassword() {
        EditText inputEmail = new EditText(this);
        inputEmail.setHint("Ingresa tu correo");

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Recuperar contraseña")
                .setCustomView(inputEmail)
                .setConfirmText("Enviar")
                .setCancelText("Cancelar")
                .setConfirmClickListener(sDialog -> {
                    String email = inputEmail.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        showAlert("Por favor, ingresa tu correo electrónico.", SweetAlertDialog.WARNING_TYPE);
                        return;
                    }

                    mAuth.sendPasswordResetEmail(email)
                            .addOnSuccessListener(aVoid -> {
                                showAlert("Se ha enviado un correo para restablecer tu contraseña.", SweetAlertDialog.SUCCESS_TYPE);
                                sDialog.dismissWithAnimation();
                            })
                            .addOnFailureListener(e -> showAlert("Error: " + e.getMessage(), SweetAlertDialog.ERROR_TYPE));
                })
                .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                .show();
    }

    private void showAlert(String message, int type) {
        new SweetAlertDialog(this, type)
                .setTitleText(message)
                .setConfirmText("OK")
                .show();
    }
}
