package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private List<Product> productList; // Lista de productos
    private ProductAdapter productAdapter; // Adaptador para el RecyclerView
    private EditText editTextProductName; // EditText para ingresar el nombre del producto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar la lista de productos
        productList = new ArrayList<>();
        productList.add(new Product("Producto 1 - Precio: 2$"));
        productList.add(new Product("Producto 2 - Precio: 4$"));

        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);
        editTextProductName = findViewById(R.id.editTextProductName);

        // boton para agregar productos
        findViewById(R.id.buttonAddProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productName = editTextProductName.getText().toString().trim();
                if (!productName.isEmpty()) {
                    productList.add(new Product(productName));
                    productAdapter.notifyItemInserted(productList.size() - 1);
                    editTextProductName.setText("");
                    Toast.makeText(ProductListActivity.this, R.string.toast_product_added, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductListActivity.this, R.string.toast_product_add_error, Toast.LENGTH_SHORT).show();

                }
            }
        });


        findViewById(R.id.buttonBackToMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
