package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private List<Product> productList;
    private List<Product> filteredProductList;
    private ProductAdapter productAdapter;
    private EditText editTextSearchName, editTextSearchCategory;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Inicializar vistas
        editTextSearchName = findViewById(R.id.editTextSearchName);
        editTextSearchCategory = findViewById(R.id.editTextSearchCategory);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button buttonBackToMain = findViewById(R.id.buttonBackToMain);
        buttonBackToMain.setOnClickListener(v -> {
            // Regresar al MainActivity
            Intent intent = new Intent(ProductListActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Evitar volver aquí con el botón "Atrás"
        });

        database = FirebaseDatabase.getInstance().getReference("products");

        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        productAdapter = new ProductAdapter(filteredProductList, position -> {
            Product product = filteredProductList.get(position);
            // Eliminar producto
            new AlertDialog.Builder(this)
                    .setTitle("Eliminar producto")
                    .setMessage("¿Deseas eliminar este producto?")
                    .setPositiveButton("Sí", (dialog, which) -> database.child(product.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                productList.remove(product);
                                filteredProductList.remove(position);
                                productAdapter.notifyItemRemoved(position);
                                Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar producto", Toast.LENGTH_SHORT).show()))
                    .setNegativeButton("No", null)
                    .show();
        }, position -> {
            Product productToEdit = filteredProductList.get(position);
            showEditDialog(productToEdit);
        });

        recyclerView.setAdapter(productAdapter);
        loadProductsFromFirebase();
        configureSearchFilters();
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonAddProduct.setOnClickListener(v -> {
            String productName = ((EditText) findViewById(R.id.editTextProductName)).getText().toString().trim();
            String productCategory = ((EditText) findViewById(R.id.editTextProductCategory)).getText().toString().trim();

            if (productName.isEmpty() || productCategory.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Agregar producto a Firebase
            String productId = database.push().getKey();
            if (productId != null) {
                Product newProduct = new Product(productId, productName, 0.0, productCategory, null);
                database.child(productId).setValue(newProduct)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show();
                            // Limpia los campos después de agregar
                            ((EditText) findViewById(R.id.editTextProductName)).setText("");
                            ((EditText) findViewById(R.id.editTextProductCategory)).setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al agregar el producto", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Error al generar ID para el producto", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadProductsFromFirebase() {
        database.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                filteredProductList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                filteredProductList.addAll(productList);
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductListActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configureSearchFilters() {
        editTextSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(editTextSearchName.getText().toString(), editTextSearchCategory.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextSearchCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(editTextSearchName.getText().toString(), editTextSearchCategory.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void showEditDialog(Product product) {
        // Crear el constructor del diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Producto");

        // cuadro de diálogo
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_edit_product, null);

        // Verificar que las vistas existan
        EditText editName = view.findViewById(R.id.editTextEditName);
        EditText editCategory = view.findViewById(R.id.editTextEditCategory);

        if (editName == null || editCategory == null) {
            throw new IllegalStateException("Las vistas no se encontraron en dialog_edit_product.xml");
        }

        // Configurar los campos con los datos actuales
        editName.setText(product.getName());
        editCategory.setText(product.getCategory());

        // Configurar la vista inflada en el diálogo
        builder.setView(view);

        // Botón Guardar
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = editName.getText().toString().trim();
            String newCategory = editCategory.getText().toString().trim();

            if (!newName.isEmpty() && !newCategory.isEmpty()) {
                product.setName(newName);
                product.setCategory(newCategory);

                // Actualizar en Firebase
                database.child(product.getId()).setValue(product)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar producto", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Cancelar
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        // Mostrar el diálogo
        builder.create().show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterProducts(String nameQuery, String categoryQuery) {
        filteredProductList.clear();
        for (Product product : productList) {
            boolean matchesName = product.getName().toLowerCase().contains(nameQuery.toLowerCase());
            boolean matchesCategory = product.getCategory().toLowerCase().contains(categoryQuery.toLowerCase());
            if (matchesName && matchesCategory) {
                filteredProductList.add(product);
            }
        }
        productAdapter.notifyDataSetChanged();
    }
}
