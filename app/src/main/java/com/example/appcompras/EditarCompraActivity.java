package com.example.appcompras;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarCompraActivity extends AppCompatActivity {

    private TextInputEditText editTextDescripcion;
    private TextInputEditText editTextCantidad;
    private Button buttonGuardarCambios;

    private String idDocumento;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_compra);

        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextCantidad = findViewById(R.id.editTextCantidad);
        buttonGuardarCambios = findViewById(R.id.buttonGuardarCambios);

        db = FirebaseFirestore.getInstance();

        // Recuperar datos del Intent
        if (getIntent() != null && getIntent().hasExtra("idDocumento")) {
            idDocumento = getIntent().getStringExtra("idDocumento");
            String descripcion = getIntent().getStringExtra("descripcion");
            String cantidad = getIntent().getStringExtra("cantidad");

            // Poblar los EditText
            if (descripcion != null) {
                editTextDescripcion.setText(descripcion);
            }
            if (cantidad != null) {
                editTextCantidad.setText(cantidad);
            }
        } else {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buttonGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarCompra();
            }
        });
    }

    private void actualizarCompra() {
        String descripcionStr = editTextDescripcion.getText().toString().trim();
        String cantidadStr = editTextCantidad.getText().toString().trim();

        if (TextUtils.isEmpty(descripcionStr)) {
            editTextDescripcion.setError("La descripción es obligatoria");
            return;
        }

        if (TextUtils.isEmpty(cantidadStr)) {
            editTextCantidad.setError("La cantidad es obligatoria");
            return;
        }

        // Deshabilitar botón para evitar múltiples clics
        buttonGuardarCambios.setEnabled(false);

        Map<String, Object> updates = new HashMap<>();
        updates.put("descripcion", descripcionStr);
        updates.put("cantidad", cantidadStr);

        db.collection("compras").document(idDocumento)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarCompraActivity.this, "Compra actualizada exitosamente", Toast.LENGTH_SHORT).show();
                    finish(); // Regresar al historial
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditarCompraActivity.this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Rehabilitar botón en caso de error
                    buttonGuardarCambios.setEnabled(true);
                });
    }
}
