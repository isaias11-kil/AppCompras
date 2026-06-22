package com.example.appcompras;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarCompraActivity extends AppCompatActivity {

    private EditText etActividad, etResponsable, etMontoEntregado, etObservaciones;
    private Button btnActualizarCompra;
    private FirebaseFirestore db;
    private String idDocumento;
    private RecyclerView rvProductosNuevos;
    private ProductoAdapter productoAdapter;
    private List<Producto> listaProductos = new ArrayList<>();
    private TextView tvTotalCalculado;
    private double totalGastado = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_compra);

        db = FirebaseFirestore.getInstance();

        etActividad = findViewById(R.id.etActividad);
        etResponsable = findViewById(R.id.etResponsable);
        etMontoEntregado = findViewById(R.id.etMontoEntregado);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnActualizarCompra = findViewById(R.id.btnActualizarCompra);
        tvTotalCalculado = findViewById(R.id.tvTotalCalculado);

        // Hide add product section for simplicity in edit view, just show existing
        findViewById(R.id.etDescProducto).setVisibility(android.view.View.GONE);
        findViewById(R.id.etCantProducto).setVisibility(android.view.View.GONE);
        findViewById(R.id.etPrecioProducto).setVisibility(android.view.View.GONE);
        findViewById(R.id.btnAgregarProducto).setVisibility(android.view.View.GONE);

        rvProductosNuevos = findViewById(R.id.rvProductosNuevos);
        rvProductosNuevos.setLayoutManager(new LinearLayoutManager(this));
        productoAdapter = new ProductoAdapter(listaProductos);
        rvProductosNuevos.setAdapter(productoAdapter);

        if (getIntent().getExtras() != null) {
            idDocumento = getIntent().getStringExtra("idDocumento");
            etActividad.setText(getIntent().getStringExtra("actividad"));
            etResponsable.setText(getIntent().getStringExtra("responsable"));
            etMontoEntregado.setText(String.valueOf(getIntent().getDoubleExtra("montoEntregado", 0.0)));
            etObservaciones.setText(getIntent().getStringExtra("observaciones"));

            cargarProductos();
        }

        btnActualizarCompra.setOnClickListener(v -> actualizarCompra());
    }

    private void cargarProductos() {
        if (idDocumento == null) return;

        db.collection("compras").document(idDocumento).collection("productos")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                listaProductos.clear();
                totalGastado = 0.0;
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    Producto p = doc.toObject(Producto.class);
                    listaProductos.add(p);
                    totalGastado += p.getTotal();
                }
                productoAdapter.notifyDataSetChanged();

                double monto = 0.0;
                try {
                    monto = Double.parseDouble(etMontoEntregado.getText().toString());
                } catch(Exception ignored) {}

                tvTotalCalculado.setText(String.format("Total Gastado: $%.2f | Diferencia: $%.2f", totalGastado, monto - totalGastado));
            });
    }

    private void actualizarCompra() {
        String actividad = etActividad.getText().toString().trim();
        String responsable = etResponsable.getText().toString().trim();
        String montoStr = etMontoEntregado.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        if (TextUtils.isEmpty(actividad) || TextUtils.isEmpty(responsable) || TextUtils.isEmpty(montoStr)) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idDocumento == null) {
            Toast.makeText(this, "Error: Documento no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        btnActualizarCompra.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("actividad", actividad);
        updates.put("responsable", responsable);
        updates.put("montoEntregado", monto);
        updates.put("diferencia", monto - totalGastado);
        updates.put("observaciones", observaciones);

        db.collection("compras").document(idDocumento)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarCompraActivity.this, "Compra actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarCompraActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    btnActualizarCompra.setEnabled(true);
                });
    }
}
