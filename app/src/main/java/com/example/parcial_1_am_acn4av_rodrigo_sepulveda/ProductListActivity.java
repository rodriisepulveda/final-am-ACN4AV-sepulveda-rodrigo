package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;
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
import cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private List<Product> productList;
    private List<Product> filteredProductList;
    private ProductAdapter productAdapter;
    private EditText editTextSearchName;
    private Spinner spinnerCategoryFilter;
    private DatabaseReference database;
    private List<String> categoryList;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        editTextSearchName = findViewById(R.id.editTextSearchName);
        spinnerCategoryFilter = findViewById(R.id.spinnerCategoryFilter);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        database = FirebaseDatabase.getInstance().getReference("products");
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        productAdapter = new ProductAdapter(filteredProductList,
                new ProductAdapter.OnProductClickListener() {
                    @Override
                    public void onProductClick(int position) {
                        Product product = filteredProductList.get(position);
                        confirmDeleteProduct(product, position);
                    }
                },
                new ProductAdapter.OnProductEditListener() {
                    @Override
                    public void onProductEdit(int position) {
                        Product productToEdit = filteredProductList.get(position);
                        Intent intent = new Intent(ProductListActivity.this, EditProductActivity.class);
                        intent.putExtra("PRODUCT_ID", productToEdit.getId());
                        startActivity(intent);
                    }
                }
        );


        ;



        recyclerView.setAdapter(productAdapter);

        loadCategoriesFromFirebase();
        loadProductsFromFirebase();
        configureSearchFilters();

        // Redirigir a la nueva actividad para agregar productos
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(ProductListActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    private void loadCategoriesFromFirebase() {
        categoryList = new ArrayList<>();
        categoryList.add("Todas");

        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null && !categoryList.contains(product.getCategory())) {
                        categoryList.add(product.getCategory());
                    }
                }

                if (categoryAdapter != null) {
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showAlert("Error al cargar categorías", SweetAlertDialog.ERROR_TYPE);
            }
        });

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryList);
        spinnerCategoryFilter.setAdapter(categoryAdapter);
        spinnerCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterProducts(editTextSearchName.getText().toString(), categoryList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterProducts(editTextSearchName.getText().toString(), "Todas");
            }
        });
    }

    private void loadProductsFromFirebase() {
        database.addValueEventListener(new ValueEventListener() {
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
                showAlert("Error al cargar productos", SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

    private void confirmDeleteProduct(Product product, int position) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Eliminar producto?")
                .setContentText("No podrás recuperarlo después")
                .setConfirmText("Eliminar")
                .setCancelText("Cancelar")
                .setConfirmClickListener(sDialog -> {
                    database.child(product.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                productList.remove(product);
                                filteredProductList.remove(position);
                                productAdapter.notifyItemRemoved(position);
                                showAlert("Producto eliminado", SweetAlertDialog.SUCCESS_TYPE);
                            })
                            .addOnFailureListener(e -> showAlert("Error al eliminar producto", SweetAlertDialog.ERROR_TYPE));
                    sDialog.dismissWithAnimation();
                })
                .show();
    }

    private void configureSearchFilters() {
        editTextSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(editTextSearchName.getText().toString(), spinnerCategoryFilter.getSelectedItem().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterProducts(String nameQuery, String categoryQuery) {
        filteredProductList.clear();

        for (Product product : productList) {
            boolean matchesName = product.getName().toLowerCase().contains(nameQuery.toLowerCase());
            boolean matchesCategory = categoryQuery.equals("Todas") || product.getCategory().equalsIgnoreCase(categoryQuery);

            if (matchesName && matchesCategory) {
                filteredProductList.add(product);
            }
        }

        productAdapter.notifyDataSetChanged();
    }

    private void showAlert(String message, int type) {
        new SweetAlertDialog(this, type)
                .setTitleText(message)
                .show();
    }
}
