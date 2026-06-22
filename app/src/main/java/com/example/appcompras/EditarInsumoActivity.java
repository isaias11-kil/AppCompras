package com.example.appcompras;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarInsumoActivity extends AppCompatActivity {

    private EditText etProducto, etCantidad, etUnidadMedida, etObservaciones;
    private Button btnActualizarInsumo;
    private FirebaseFirestore db;
    private String idDocumento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_insumo);

        db = FirebaseFirestore.getInstance();

        etProducto = findViewById(R.id.etProducto);
        etCantidad = findViewById(R.id.etCantidad);
        etUnidadMedida = findViewById(R.id.etUnidadMedida);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnActualizarInsumo = findViewById(R.id.btnActualizarInsumo);

        if (getIntent().getExtras() != null) {
            idDocumento = getIntent().getStringExtra("idDocumento");
            etProducto.setText(getIntent().getStringExtra("producto"));
            etCantidad.setText(String.valueOf(getIntent().getDoubleExtra("cantidad", 0.0)));
            etUnidadMedida.setText(getIntent().getStringExtra("unidadMedida"));
            etObservaciones.setText(getIntent().getStringExtra("observaciones"));
        }

        btnActualizarInsumo.setOnClickListener(v -> actualizarInsumo());
    }

    private void actualizarInsumo() {
        String producto = etProducto.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();
        String unidadMedida = etUnidadMedida.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        if (TextUtils.isEmpty(producto) || TextUtils.isEmpty(cantidadStr) || TextUtils.isEmpty(unidadMedida)) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idDocumento == null) {
            Toast.makeText(this, "Error: Documento no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        btnActualizarInsumo.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("producto", producto);
        updates.put("cantidad", cantidad);
        updates.put("unidadMedida", unidadMedida);
        updates.put("observaciones", observaciones);

        db.collection("insumos").document(idDocumento)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarInsumoActivity.this, "Insumo actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarInsumoActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    btnActualizarInsumo.setEnabled(true);
                });
    }
}
