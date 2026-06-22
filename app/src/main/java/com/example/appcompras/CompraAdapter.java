package com.example.appcompras;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CompraAdapter extends RecyclerView.Adapter<CompraAdapter.CompraViewHolder> {

    private List<Compra> compraList;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public CompraAdapter(List<Compra> compraList) {
        this.compraList = compraList;
    }

    public void setCompras(List<Compra> compras) {
        this.compraList = compras;
        notifyDataSetChanged();
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

        holder.tvActividad.setText(compra.getActividad());
        holder.tvResponsable.setText(compra.getResponsable());
        holder.tvTotalGastado.setText("$" + compra.getTotalGastado());

        Timestamp timestamp = compra.getFecha();
        if (timestamp != null) {
            Date date = timestamp.toDate();
            holder.tvFecha.setText(sdf.format(date));
        } else {
            holder.tvFecha.setText("");
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditarCompraActivity.class);
            intent.putExtra("idDocumento", compra.getIdDocumento());
            intent.putExtra("actividad", compra.getActividad());
            intent.putExtra("responsable", compra.getResponsable());
            intent.putExtra("montoEntregado", compra.getMontoEntregado());
            intent.putExtra("observaciones", compra.getObservaciones());
            v.getContext().startActivity(intent);
        });

        holder.btnItemGenerarPdf.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("compras").document(compra.getIdDocumento()).collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Producto> productos = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        productos.add(doc.toObject(Producto.class));
                    }
                    PdfGenerator.generarPdfLiquidacion(v.getContext(), compra, productos);
                })
                .addOnFailureListener(e -> {
                    PdfGenerator.generarPdfLiquidacion(v.getContext(), compra, new ArrayList<>());
                });
        });
    }

    @Override
    public int getItemCount() {
        return compraList != null ? compraList.size() : 0;
    }

    public static class CompraViewHolder extends RecyclerView.ViewHolder {
        TextView tvActividad;
        TextView tvResponsable;
        TextView tvFecha;
        TextView tvTotalGastado;
        Button btnItemGenerarPdf;

        public CompraViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActividad = itemView.findViewById(R.id.tvActividad);
            tvResponsable = itemView.findViewById(R.id.tvResponsable);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvTotalGastado = itemView.findViewById(R.id.tvTotalGastado);
            btnItemGenerarPdf = itemView.findViewById(R.id.btnItemGenerarPdf);
        }
    }
}
