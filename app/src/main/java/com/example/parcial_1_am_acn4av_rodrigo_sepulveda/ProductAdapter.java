package com.example.parcial_1_am_acn4av_rodrigo_sepulveda;

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

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.onProductClickListener = listener;
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

        // Configurar el botón de eliminar
        holder.buttonDeleteProduct.setOnClickListener(v -> {
            if (onProductClickListener != null) {
                onProductClickListener.onProductClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewProductName;
        final ImageButton buttonDeleteProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            buttonDeleteProduct = itemView.findViewById(R.id.buttonDeleteProduct);
        }
    }

    // Interfaz para manejar eventos de clic
    public interface OnProductClickListener {
        void onProductClick(int position);
    }
}
