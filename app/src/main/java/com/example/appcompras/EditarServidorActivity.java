package com.example.appcompras;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditarServidorActivity extends AppCompatActivity {

    private EditText etTipoServidor;
    private EditText etProcesador;
    private EditText etRam;
    private EditText etAlmacenamiento;
    private EditText etDireccionIp;
    private EditText etArea;
    private Button btnActualizarServidor;

    private FirebaseFirestore db;
    private static final String TAG = "EditarServidor";

    private String idDocumento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_servidor);

        // Inicializar vistas
        etTipoServidor = findViewById(R.id.etTipoServidor);
        etProcesador = findViewById(R.id.etProcesador);
        etRam = findViewById(R.id.etRam);
        etAlmacenamiento = findViewById(R.id.etAlmacenamiento);
        etDireccionIp = findViewById(R.id.etDireccionIp);
        etArea = findViewById(R.id.etArea);
        btnActualizarServidor = findViewById(R.id.btnActualizarServidor);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();

        // Obtener datos del Intent
        idDocumento = getIntent().getStringExtra("idDocumento");
        etTipoServidor.setText(getIntent().getStringExtra("tipoServidor"));
        etProcesador.setText(getIntent().getStringExtra("procesador"));
        etRam.setText(getIntent().getStringExtra("ram"));
        etAlmacenamiento.setText(getIntent().getStringExtra("almacenamiento"));
        etDireccionIp.setText(getIntent().getStringExtra("direccionIp"));
        etArea.setText(getIntent().getStringExtra("area"));

        if (idDocumento == null || idDocumento.isEmpty()) {
            Toast.makeText(this, "Error: ID de servidor no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar botón
        btnActualizarServidor.setOnClickListener(v -> actualizarServidor());
    }

    private void actualizarServidor() {
        String tipoServidor = etTipoServidor.getText().toString().trim();
        String procesador = etProcesador.getText().toString().trim();
        String ram = etRam.getText().toString().trim();
        String almacenamiento = etAlmacenamiento.getText().toString().trim();
        String direccionIp = etDireccionIp.getText().toString().trim();
        String area = etArea.getText().toString().trim();

        if (TextUtils.isEmpty(tipoServidor)) {
            etTipoServidor.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(procesador)) {
            etProcesador.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(ram)) {
            etRam.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(almacenamiento)) {
            etAlmacenamiento.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(direccionIp)) {
            etDireccionIp.setError("Campo requerido");
            return;
        }
        if (TextUtils.isEmpty(area)) {
            etArea.setError("Campo requerido");
            return;
        }

        // Disable button during network request
        btnActualizarServidor.setEnabled(false);

        Map<String, Object> servidorUpdates = new HashMap<>();
        servidorUpdates.put("tipoServidor", tipoServidor);
        servidorUpdates.put("procesador", procesador);
        servidorUpdates.put("ram", ram);
        servidorUpdates.put("almacenamiento", almacenamiento);
        servidorUpdates.put("direccionIp", direccionIp);
        servidorUpdates.put("area", area);

        db.collection("servidores")
                .document(idDocumento)
                .update(servidorUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditarServidorActivity.this, "Servidor actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar la actividad
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar el servidor", e);
                    Toast.makeText(EditarServidorActivity.this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Re-enable button on failure
                    btnActualizarServidor.setEnabled(true);
                });
    }
}
