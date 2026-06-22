package com.example.appcompras;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class RegistrarServidorActivity extends AppCompatActivity {

    private EditText etTipoServidor;
    private EditText etProcesador;
    private EditText etRam;
    private EditText etAlmacenamiento;
    private EditText etDireccionIp;
    private EditText etArea;
    private Button btnGuardarServidor;

    private FirebaseFirestore db;
    private static final String TAG = "RegistrarServidor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_servidor);

        // Inicializar vistas
        etTipoServidor = findViewById(R.id.etTipoServidor);
        etProcesador = findViewById(R.id.etProcesador);
        etRam = findViewById(R.id.etRam);
        etAlmacenamiento = findViewById(R.id.etAlmacenamiento);
        etDireccionIp = findViewById(R.id.etDireccionIp);
        etArea = findViewById(R.id.etArea);
        btnGuardarServidor = findViewById(R.id.btnGuardarServidor);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();

        // Configurar botón
        btnGuardarServidor.setOnClickListener(v -> guardarServidor());
    }

    private void guardarServidor() {
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
        btnGuardarServidor.setEnabled(false);

        String idDocumento = UUID.randomUUID().toString();
        Servidor servidor = new Servidor(idDocumento, tipoServidor, procesador, ram, almacenamiento, direccionIp, area);

        db.collection("servidores")
                .document(idDocumento)
                .set(servidor)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegistrarServidorActivity.this, "Servidor guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar la actividad
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar el servidor", e);
                    Toast.makeText(RegistrarServidorActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Re-enable button on failure
                    btnGuardarServidor.setEnabled(true);
                });
    }
}
