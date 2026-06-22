package com.example.appcompras;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class InsumoAdapter extends RecyclerView.Adapter<InsumoAdapter.ViewHolder> {

    private List<Insumo> insumos;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public InsumoAdapter(List<Insumo> insumos) {
        this.insumos = insumos;
    }

    public void setInsumos(List<Insumo> insumos) {
        this.insumos = insumos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_insumo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Insumo insumo = insumos.get(position);
        holder.tvProducto.setText(insumo.getProducto());
        holder.tvCantidad.setText(String.valueOf(insumo.getCantidad()) + " " + insumo.getUnidadMedida());

        if (insumo.getFecha() != null) {
            holder.tvFecha.setText(sdf.format(insumo.getFecha().toDate()));
        } else {
            holder.tvFecha.setText("Fecha no disponible");
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EditarInsumoActivity.class);
            intent.putExtra("idDocumento", insumo.getIdDocumento());
            intent.putExtra("producto", insumo.getProducto());
            intent.putExtra("cantidad", insumo.getCantidad());
            intent.putExtra("unidadMedida", insumo.getUnidadMedida());
            intent.putExtra("observaciones", insumo.getObservaciones());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return insumos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProducto;
        TextView tvCantidad;
        TextView tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProducto = itemView.findViewById(R.id.tvProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
