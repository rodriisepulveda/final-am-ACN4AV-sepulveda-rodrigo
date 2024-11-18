package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductListActivity extends AppCompatActivity {
    private List<Product> productList;
    private List<Product> filteredProductList;
    private ProductAdapter productAdapter;
    private EditText editTextProductName, editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextSearch = findViewById(R.id.editTextSearch);

        // Inicializar lista de productos
        productList = new ArrayList<>();
        productList.add(new Product("Producto 1"));
        productList.add(new Product("Producto 2"));

        filteredProductList = new ArrayList<>(productList);

        productAdapter = new ProductAdapter(filteredProductList, position -> new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Deseas eliminar este producto?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    productList.remove(filteredProductList.get(position));
                    filteredProductList.remove(position);
                    productAdapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();

                    if (productList.isEmpty()) {
                        Toast.makeText(this, "No hay productos disponibles", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show());

        recyclerView.setAdapter(productAdapter);

        // Búsqueda en tiempo real
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Botón para agregar productos
        findViewById(R.id.buttonAddProduct).setOnClickListener(v -> {
            String productName = editTextProductName.getText().toString().trim();
            if (!productName.isEmpty()) {
                productList.add(new Product(productName));
                filterProducts(editTextSearch.getText().toString()); // Refrescar con el filtro aplicado
                recyclerView.scrollToPosition(filteredProductList.size() - 1);
                editTextProductName.setText("");
                Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Por favor, ingresa un nombre válido", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para volver a la actividad principal
        findViewById(R.id.buttonBackToMain).setOnClickListener(v -> finish());
    }

    private void filterProducts(String query) {
        filteredProductList.clear();
        if (query.isEmpty()) {
            filteredProductList.addAll(productList);
        } else {
            filteredProductList.addAll(productList.stream()
                    .filter(product -> product.getName().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList()));
        }
        productAdapter.notifyDataSetChanged();
    }
}
