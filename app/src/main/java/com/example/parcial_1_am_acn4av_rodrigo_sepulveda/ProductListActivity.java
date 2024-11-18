package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
    private EditText editTextProductName, editTextProductCategory;
    private EditText editTextSearchName, editTextSearchCategory;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Inicializar vistas
        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductCategory = findViewById(R.id.editTextProductCategory);
        editTextSearchName = findViewById(R.id.editTextSearchName);
        editTextSearchCategory = findViewById(R.id.editTextSearchCategory);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance().getReference("products");

        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        productAdapter = new ProductAdapter(filteredProductList, position -> {
            Product product = filteredProductList.get(position);
            // Botón de eliminar
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

        findViewById(R.id.buttonAddProduct).setOnClickListener(v -> addProduct());
        findViewById(R.id.buttonBackToMain).setOnClickListener(v -> finish());
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

    private void addProduct() {
        String productName = editTextProductName.getText().toString().trim();
        String productCategory = editTextProductCategory.getText().toString().trim();
        if (!productName.isEmpty() && !productCategory.isEmpty()) {
            String id = database.push().getKey();
            Product newProduct = new Product(id, productName, 100.0, productCategory);

            assert id != null;
            database.child(id).setValue(newProduct)
                    .addOnSuccessListener(aVoid -> {
                        productList.add(newProduct);
                        filterProducts(editTextSearchName.getText().toString(), editTextSearchCategory.getText().toString());
                        editTextProductName.setText("");
                        editTextProductCategory.setText("");
                        Toast.makeText(this, "Producto agregado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al agregar producto", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Por favor, ingresa un nombre y categoría válidos", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Producto");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_product, null);
        EditText editName = view.findViewById(R.id.editTextEditName);
        EditText editCategory = view.findViewById(R.id.editTextEditCategory);

        editName.setText(product.getName());
        editCategory.setText(product.getCategory());

        builder.setView(view);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = editName.getText().toString().trim();
            String newCategory = editCategory.getText().toString().trim();

            if (!newName.isEmpty() && !newCategory.isEmpty()) {
                product.setName(newName);
                product.setCategory(newCategory);

                database.child(product.getId()).setValue(product)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar producto", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
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
