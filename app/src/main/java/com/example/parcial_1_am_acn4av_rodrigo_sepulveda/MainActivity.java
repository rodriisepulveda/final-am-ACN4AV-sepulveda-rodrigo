package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private TextView textViewWelcome;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();

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
            showToast("Usuario no autenticado");
            redirectToLogin();
        }

        // Navegación a lista de productos
        buttonShowProducts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        // Funcionalidad en desarrollo
        buttonAddProduct.setOnClickListener(v ->
                showToast(getString(R.string.toast_functionality_development))
        );

        buttonDeleteProduct.setOnClickListener(v ->
                showToast(getString(R.string.toast_functionality_development))
        );

        // Editar perfil
        buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Cerrar sesión
        buttonLogout.setOnClickListener(v -> logoutUser());
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
                        showToast("No se encontraron datos del usuario en Firestore");
                    }
                })
                .addOnFailureListener(e -> showToast("Error al cargar datos: " + e.getMessage()));
    }

    private void logoutUser() {
        mAuth.signOut();
        showToast("Sesión cerrada");
        redirectToLogin();
    }

    private void redirectToLogin() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

