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
import com.google.firebase.firestore.FirebaseFirestore;

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

        // Referencias UI
        textViewWelcome = findViewById(R.id.textViewWelcome);
        Button buttonShowProducts = findViewById(R.id.buttonShowProducts);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);
        Button buttonDeleteProduct = findViewById(R.id.buttonDeleteProduct);
        Button buttonLogout = findViewById(R.id.buttonLogout);

        // Cargar datos del usuario
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserData(user.getUid());
        }

        // Navegación a lista de productos
        buttonShowProducts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        // Funcionalidad en desarrollo
        buttonAddProduct.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, getString(R.string.toast_functionality_development), Toast.LENGTH_SHORT).show()
        );

        buttonDeleteProduct.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, getString(R.string.toast_functionality_development), Toast.LENGTH_SHORT).show()
        );

        // Cerrar sesión
        buttonLogout.setOnClickListener(v -> logoutUser());
    }

    @SuppressLint("SetTextI18n")
    private void loadUserData(String userId) {
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombre = documentSnapshot.getString("nombre");
                        textViewWelcome.setText("Bienvenido, " + (nombre != null ? nombre : "Usuario"));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }
}
