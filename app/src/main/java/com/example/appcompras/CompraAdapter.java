package com.example.appcompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CompraAdapter extends RecyclerView.Adapter<CompraAdapter.CompraViewHolder> {

    private List<Compra> compraList;

    public CompraAdapter(List<Compra> compraList) {
        this.compraList = compraList;
    }

    @NonNull
    @Override
    public CompraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_compra, parent, false);
        return new CompraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompraViewHolder holder, int position) {
        Compra compra = compraList.get(position);

        holder.textViewDescripcion.setText(compra.getDescripcion());

        if (compra.getCantidad() != null && !compra.getCantidad().isEmpty()) {
            holder.textViewCantidad.setText("Cantidad: " + compra.getCantidad());
        } else {
            holder.textViewCantidad.setText("Cantidad: N/A");
        }

        // Formatear Timestamp a String
        Timestamp timestamp = compra.getFecha();
        if (timestamp != null) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.textViewFecha.setText(sdf.format(date));
        } else {
            holder.textViewFecha.setText("");
        }

        // Cargar imagen con Glide
        if (compra.getImageUrl() != null && !compra.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(compra.getImageUrl())
                    .centerCrop()
                    .into(holder.imageViewFactura);
        } else {
            // Se puede establecer un placeholder aquí si se desea
            holder.imageViewFactura.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditarCompraActivity.class);
                intent.putExtra("idDocumento", compra.getIdDocumento());
                intent.putExtra("descripcion", compra.getDescripcion());
                if (compra.getCantidad() != null) {
                    intent.putExtra("cantidad", compra.getCantidad());
                } else {
                    intent.putExtra("cantidad", "");
                }
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return compraList != null ? compraList.size() : 0;
    }

    public static class CompraViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFactura;
        TextView textViewDescripcion;
        TextView textViewCantidad;
        TextView textViewFecha;

        public CompraViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFactura = itemView.findViewById(R.id.imageViewFactura);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewCantidad = itemView.findViewById(R.id.textViewCantidad);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
        }
    }
}
