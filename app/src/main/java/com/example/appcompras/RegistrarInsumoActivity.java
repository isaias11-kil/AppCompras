package com.example.appcompras;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class RegistrarInsumoActivity extends AppCompatActivity {

    private EditText etProducto, etCantidad, etUnidadMedida, etObservaciones;
    private Button btnGuardarInsumo;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_insumo);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etProducto = findViewById(R.id.etProducto);
        etCantidad = findViewById(R.id.etCantidad);
        etUnidadMedida = findViewById(R.id.etUnidadMedida);
        etObservaciones = findViewById(R.id.etObservaciones);
        btnGuardarInsumo = findViewById(R.id.btnGuardarInsumo);

        btnGuardarInsumo.setOnClickListener(v -> guardarInsumo());
    }

    private void guardarInsumo() {
        String producto = etProducto.getText().toString().trim();
        String cantidadStr = etCantidad.getText().toString().trim();
        String unidadMedida = etUnidadMedida.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        if (TextUtils.isEmpty(producto) || TextUtils.isEmpty(cantidadStr) || TextUtils.isEmpty(unidadMedida)) {
            Toast.makeText(this, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        double cantidad;
        try {
            cantidad = Double.parseDouble(cantidadStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardarInsumo.setEnabled(false);

        String idDocumento = db.collection("insumos").document().getId();
        Timestamp fecha = new Timestamp(new Date());

        Insumo insumo = new Insumo(fecha, producto, cantidad, unidadMedida, observaciones, user.getUid());

        db.collection("insumos").document(idDocumento).set(insumo)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegistrarInsumoActivity.this, "Insumo guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistrarInsumoActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnGuardarInsumo.setEnabled(true);
                });
    }
}
