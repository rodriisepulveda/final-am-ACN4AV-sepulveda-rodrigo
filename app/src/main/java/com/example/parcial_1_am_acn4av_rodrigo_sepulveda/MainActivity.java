package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // botón para mostrar la lista de productos
        findViewById(R.id.buttonShowProducts).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            startActivity(intent);
        });

        //  botón "Agregar Producto"
        findViewById(R.id.buttonAddProduct).setOnClickListener(v ->
                Toast.makeText(MainActivity.this, getString(R.string.toast_functionality_development), Toast.LENGTH_SHORT).show()
        );

        // "Eliminar Producto"
        findViewById(R.id.buttonDeleteProduct).setOnClickListener(v ->
                Toast.makeText(MainActivity.this, getString(R.string.toast_functionality_development), Toast.LENGTH_SHORT).show()
        );

        // Inicializar Firebase Database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("test_connection");

        // Probar la conexión escribiendo un valor en Firebase
        database.setValue("Conexión exitosa")
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Conexión con Firebase correcta", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al conectar con Firebase", Toast.LENGTH_SHORT).show()
                );
    }
}
