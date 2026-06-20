package com.example.appcompras;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.content.Intent;
public class MainActivity extends AppCompatActivity {

    // 1. Declarar las variables para la interfaz y Firebase
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // 3. Vincular las variables con los IDs que creaste en el XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // 4. Detectar el clic en el botón de Ingresar
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el texto que el usuario escribió
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validar que no estén vacíos
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Llamar al método de Firebase
                iniciarSesion(email, password);
            }
        });

        // Configurar el botón de registro (por ahora solo mostrará un mensaje)
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Viajar a la pantalla de registro
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    // 5. Método para iniciar sesión con Firebase
    private void iniciarSesion(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // ¡Inicio de sesión exitoso!
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Bienvenido: " + user.getEmail(), Toast.LENGTH_LONG).show();

                        // Ir a la HomeActivity
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error (correo incorrecto, contraseña mal, o no existe)
                        Toast.makeText(MainActivity.this, "Error: Revisa tus credenciales", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}