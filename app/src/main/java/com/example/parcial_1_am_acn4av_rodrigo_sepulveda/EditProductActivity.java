package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.os.Bundle;
import android.text.TextUtils;
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
import cn.pedant.SweetAlert.SweetAlertDialog;
import java.util.ArrayList;
import java.util.List;

public class EditProductActivity extends AppCompatActivity {
    private EditText editTextName, editTextPrice;
    private Spinner spinnerCategory;
    private Button buttonSave, buttonCancel;
    private DatabaseReference database;
    private String productId;
    private List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Inicializar vistas
        editTextName = findViewById(R.id.editTextEditName);
        editTextPrice = findViewById(R.id.editTextEditPrice);
        spinnerCategory = findViewById(R.id.spinnerEditCategory);
        buttonSave = findViewById(R.id.buttonSaveEdit);
        buttonCancel = findViewById(R.id.buttonCancelEdit);

        // Obtener el ID del producto desde el intent
        productId = getIntent().getStringExtra("PRODUCT_ID");
        database = FirebaseDatabase.getInstance().getReference("products");

        // Cargar categorías desde Firebase
        loadCategories();

        // Cargar datos del producto
        loadProductData();

        // Guardar cambios en Firebase
        buttonSave.setOnClickListener(v -> updateProduct());

        // Cancelar y volver atrás
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void loadCategories() {
        categoryList = new ArrayList<>();
        categoryList.add("Seleccionar categoría");

        database.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    String category = productSnapshot.child("category").getValue(String.class);
                    if (category != null && !categoryList.contains(category)) {
                        categoryList.add(category);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(EditProductActivity.this, android.R.layout.simple_spinner_dropdown_item, categoryList);
                spinnerCategory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadProductData() {
        if (productId == null) {
            showAlert("Error al cargar el producto", SweetAlertDialog.ERROR_TYPE);
            finish();
            return;
        }

        database.child(productId).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    Double price = snapshot.child("price").getValue(Double.class);
                    String category = snapshot.child("category").getValue(String.class);

                    editTextName.setText(name);
                    editTextPrice.setText(price != null ? String.valueOf(price) : "");

                    int categoryPosition = categoryList.indexOf(category);
                    if (categoryPosition >= 0) {
                        spinnerCategory.setSelection(categoryPosition);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showAlert("Error al cargar los datos", SweetAlertDialog.ERROR_TYPE);
            }
        });
    }

    private void updateProduct() {
        String newName = editTextName.getText().toString().trim();
        String newPriceStr = editTextPrice.getText().toString().trim();
        String newCategory = spinnerCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newPriceStr) || newCategory.equals("Seleccionar categoría")) {
            showAlert("Por favor, completa todos los campos", SweetAlertDialog.WARNING_TYPE);
            return;
        }

        double newPrice = Double.parseDouble(newPriceStr);

        database.child(productId).child("name").setValue(newName);
        database.child(productId).child("price").setValue(newPrice);
        database.child(productId).child("category").setValue(newCategory)
                .addOnSuccessListener(aVoid -> {
                    showAlert("Producto actualizado correctamente", SweetAlertDialog.SUCCESS_TYPE);
                    finish();
                })
                .addOnFailureListener(e -> showAlert("Error al actualizar", SweetAlertDialog.ERROR_TYPE));
    }

    private void showAlert(String message, int type) {
        new SweetAlertDialog(this, type)
                .setTitleText(message)
                .show();
    }
}
