package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class ProductDetailActivity extends AppCompatActivity {
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Obtener los datos del intent
        String productName = getIntent().getStringExtra("productName");
        String productCategory = getIntent().getStringExtra("productCategory");
        double productPrice = getIntent().getDoubleExtra("productPrice", 0.0);
        String productImageUrl = getIntent().getStringExtra("productImageUrl");

        // Enlazar vistas
        TextView textViewName = findViewById(R.id.textViewDetailName);
        TextView textViewCategory = findViewById(R.id.textViewDetailCategory);
        TextView textViewPrice = findViewById(R.id.textViewDetailPrice);
        ImageView imageViewProduct = findViewById(R.id.imageViewDetailProduct);
        Button buttonBackToList = findViewById(R.id.buttonBackToList);

        // Establecer datos en las vistas
        textViewName.setText(productName);
        textViewCategory.setText(String.format(getString(R.string.category_label), productCategory));
        textViewPrice.setText(String.format("$ %.2f", productPrice));

        // Cargar imagen con Glide y animación
        if (productImageUrl != null && !productImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(productImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_image) // Imagen por defecto
                    .error(R.drawable.placeholder_image)
                    .into(imageViewProduct);
        } else {
            imageViewProduct.setImageResource(R.drawable.placeholder_image);
        }

        // Configurar botón de volver con animación
        buttonBackToList.setOnClickListener(v -> {
            v.animate().alpha(0.5f).setDuration(200).withEndAction(() -> {
                finish();
            });
        });
    }
}
