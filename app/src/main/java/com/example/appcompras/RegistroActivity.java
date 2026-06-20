package com.example.appcompras;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword;
    private Button btnCrearCuenta, btnVolver;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Vincular vistas
        etNombre = findViewById(R.id.etNombreRegistro);
        etEmail = findViewById(R.id.etEmailRegistro);
        etPassword = findViewById(R.id.etPasswordRegistro);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnVolver = findViewById(R.id.btnVolverLogin);

        // Acción del botón Volver
        btnVolver.setOnClickListener(v -> finish()); // finish() cierra esta pantalla y vuelve a la anterior

        // Acción del botón Registrar
        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String nombre = etNombre.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
                    Toast.makeText(RegistroActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(RegistroActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                crearCuentaFirebase(email, password);
            }
        });
    }

    private void crearCuentaFirebase(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistroActivity.this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show();
                        // Cerramos la pantalla de registro para que el usuario pueda iniciar sesión
                        finish();
                    } else {
                        Toast.makeText(RegistroActivity.this, "Error al crear la cuenta: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}