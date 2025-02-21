package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {
    private EditText editTextProductName, editTextProductPrice;
    private Spinner spinnerCategory;
    private DatabaseReference database;
    private List<String> categoryList;
    private ArrayAdapter<String> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextProductPrice = findViewById(R.id.editTextProductPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);
        Button buttonBack = findViewById(R.id.buttonBack);

        database = FirebaseDatabase.getInstance().getReference("products");

        categoryList = new ArrayList<>();
        categoryList.add("Seleccionar categoría");

        loadCategoriesFromFirebase();

        buttonAddProduct.setOnClickListener(v -> addProduct());
        buttonBack.setOnClickListener(v -> finish());
    }

    private void loadCategoriesFromFirebase() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    if (product != null && !categoryList.contains(product.getCategory())) {
                        categoryList.add(product.getCategory());
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showAlert("Error al cargar categorías", SweetAlertDialog.ERROR_TYPE);
            }
        });

        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categoryList);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void addProduct() {
        String productName = editTextProductName.getText().toString().trim();
        String productPriceStr = editTextProductPrice.getText().toString().trim();
        String productCategory = spinnerCategory.getSelectedItem().toString();

        if (productName.isEmpty() || productPriceStr.isEmpty() || productCategory.equals("Seleccionar categoría")) {
            showAlert("Por favor, completa todos los campos", SweetAlertDialog.WARNING_TYPE);
            return;
        }

        double productPrice = Double.parseDouble(productPriceStr);
        String productId = database.push().getKey();

        if (productId != null) {
            Product newProduct = new Product(productId, productName, productPrice, productCategory, null);
            database.child(productId).setValue(newProduct)
                    .addOnSuccessListener(aVoid -> {
                        showAlert("Producto agregado exitosamente", SweetAlertDialog.SUCCESS_TYPE);
                        editTextProductName.setText("");
                        editTextProductPrice.setText("");
                        spinnerCategory.setSelection(0);
                    })
                    .addOnFailureListener(e -> showAlert("Error al agregar el producto", SweetAlertDialog.ERROR_TYPE));
        } else {
            showAlert("Error al generar ID para el producto", SweetAlertDialog.ERROR_TYPE);
        }
    }

    private void showAlert(String message, int type) {
        new SweetAlertDialog(this, type)
                .setTitleText(message)
                .show();
    }
}

