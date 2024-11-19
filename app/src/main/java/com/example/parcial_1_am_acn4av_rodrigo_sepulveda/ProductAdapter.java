package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final OnProductClickListener onProductClickListener;
    private final OnProductEditListener onProductEditListener;

    public ProductAdapter(List<Product> productList, OnProductClickListener deleteListener, OnProductEditListener editListener) {
        this.productList = productList;
        this.onProductClickListener = deleteListener;
        this.onProductEditListener = editListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Mostrar el nombre del producto
        holder.textViewProductName.setText(product.getName());

        // Mostrar la categoría del producto
        holder.textViewProductCategory.setText(
                holder.itemView.getContext().getString(R.string.category_label, product.getCategory())
        );

        // Configurar clic para navegar a ProductDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("productName", product.getName());
            intent.putExtra("productCategory", product.getCategory());
            intent.putExtra("productPrice", product.getPrice());
            intent.putExtra("productImageUrl", product.getImageUrl());
            v.getContext().startActivity(intent);
        });

        // Configurar botón de eliminar producto
        holder.buttonDeleteProduct.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductClick(position);
            }
        });

        // Configurar botón de editar producto
        holder.buttonEditProduct.setOnClickListener(v -> {
            if (onProductEditListener != null) {
                onProductEditListener.onProductEdit(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewProductName;
        final TextView textViewProductCategory;
        final ImageButton buttonDeleteProduct;
        final ImageButton buttonEditProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductCategory = itemView.findViewById(R.id.textViewProductCategory);
            buttonDeleteProduct = itemView.findViewById(R.id.buttonDeleteProduct);
            buttonEditProduct = itemView.findViewById(R.id.buttonEditProduct);
        }
    }

    // Interfaz para manejar eventos de clic
    public interface OnProductClickListener {
        void onProductClick(int position);
    }

    // Interfaz para manejar eventos de edición
    public interface OnProductEditListener {
        void onProductEdit(int position);
    }
}
