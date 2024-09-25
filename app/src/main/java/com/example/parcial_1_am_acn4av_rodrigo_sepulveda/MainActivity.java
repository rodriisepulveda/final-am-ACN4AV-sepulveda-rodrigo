package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonShowProducts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
                startActivity(intent);
            }
        });

        // Evento para el botón "Agregar Producto"
        findViewById(R.id.buttonAddProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_functionality_development, Toast.LENGTH_SHORT).show();
            }
        });

        // Evento para el botón "Eliminar Producto"
        findViewById(R.id.buttonDeleteProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_functionality_development, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
