package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private TextView textViewWelcome;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        // Referencias UI
        textViewWelcome = findViewById(R.id.textViewWelcome);
        Button buttonShowProducts = findViewById(R.id.buttonShowProducts);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);
        Button buttonDeleteProduct = findViewById(R.id.buttonDeleteProduct);
        Button buttonLogout = findViewById(R.id.buttonLogout);
        Button buttonEditProfile = findViewById(R.id.buttonEditProfile);

        // Verificar si hay un usuario autenticado
        if (user != null) {
            loadUserData(user.getUid());
        } else {
            showAlert("Usuario no autenticado", SweetAlertDialog.ERROR_TYPE);
            redirectToLogin();
        }

        // Navegación a lista de productos
        buttonShowProducts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        // Funcionalidad en desarrollo
        buttonAddProduct.setOnClickListener(v ->
                showAlert(getString(R.string.toast_functionality_development), SweetAlertDialog.ERROR_TYPE)
        );

        buttonDeleteProduct.setOnClickListener(v ->
                showAlert(getString(R.string.toast_functionality_development), SweetAlertDialog.ERROR_TYPE)
        );

        // Editar perfil
        buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Cerrar sesión con confirmación
        buttonLogout.setOnClickListener(v -> confirmLogout());
    }

    @SuppressLint("SetTextI18n")
    private void loadUserData(String userId) {
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        if (nombre != null && !nombre.isEmpty()) {
                            textViewWelcome.setText("Bienvenido, " + nombre);
                        } else {
                            textViewWelcome.setText("Bienvenido, Usuario");
                        }
                    } else {
                        showAlert("No se encontraron datos del usuario en Firestore", SweetAlertDialog.WARNING_TYPE);
                    }
                })
                .addOnFailureListener(e -> showAlert("Error al cargar datos: " + e.getMessage(), SweetAlertDialog.ERROR_TYPE));
    }

    private void confirmLogout() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Cerrar sesión?")
                .setContentText("Tendrás que volver a iniciar sesión")
                .setConfirmText("Sí, salir")
                .setCancelText("Cancelar")
                .setConfirmClickListener(sDialog -> {
                    logoutUser();
                    sDialog.dismissWithAnimation();
                })
                .show();
    }

    private void logoutUser() {
        mAuth.signOut();
        showAlert("Sesión cerrada", SweetAlertDialog.SUCCESS_TYPE);
        redirectToLogin();
    }

    private void redirectToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void showAlert(String message, int type) {
        new SweetAlertDialog(this, type)
                .setTitleText(message)
                .show();
    }
}

