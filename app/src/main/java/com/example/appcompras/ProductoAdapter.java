package com.example.appcompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private List<Producto> productos;

    public ProductoAdapter(List<Producto> productos) {
        this.productos = productos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto p = productos.get(position);
        holder.tvDesc.setText(p.getDescripcion());
        holder.tvCant.setText(String.valueOf(p.getCantidad()));
        holder.tvPrecio.setText("$" + p.getPrecioUnitario());
        holder.tvTotal.setText("$" + p.getTotal());
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesc, tvCant, tvPrecio, tvTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tvProdDesc);
            tvCant = itemView.findViewById(R.id.tvProdCant);
            tvPrecio = itemView.findViewById(R.id.tvProdPrecio);
            tvTotal = itemView.findViewById(R.id.tvProdTotal);
        }
    }
}
