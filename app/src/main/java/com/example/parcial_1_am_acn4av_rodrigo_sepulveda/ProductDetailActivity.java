package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ProductDetailActivity extends AppCompatActivity {
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Obtener los datos enviados desde el intent
        String productName = getIntent().getStringExtra("productName");
        String productCategory = getIntent().getStringExtra("productCategory");
        double productPrice = getIntent().getDoubleExtra("productPrice", 0.0);
        String productImageUrl = getIntent().getStringExtra("productImageUrl");

        // Enlazar vistas
        TextView textViewName = findViewById(R.id.textViewDetailName);
        TextView textViewCategory = findViewById(R.id.textViewDetailCategory);
        TextView textViewPrice = findViewById(R.id.textViewDetailPrice);
        ImageView imageViewProduct = findViewById(R.id.imageViewDetailProduct);

        // Mostrar los datos
        textViewName.setText(productName);
        textViewCategory.setText(String.format(getString(R.string.category_label), productCategory));
        textViewPrice.setText(String.format("$ %.2f", productPrice));

        // Cargar la imagen
        Glide.with(this)
                .load(productImageUrl)
                .placeholder(R.drawable.placeholder_image) // Imagen por defecto
                .into(imageViewProduct);

        // Configurar botón de volver
        Button buttonBackToList = findViewById(R.id.buttonBackToList);
        buttonBackToList.setOnClickListener(v -> finish());
    }
}

