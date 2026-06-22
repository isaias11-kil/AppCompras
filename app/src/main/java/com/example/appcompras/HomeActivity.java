package com.example.appcompras;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeActivity extends AppCompatActivity {

    private TextView tvTotalGastado;
    private Button btnRegistrarCompra;
    private Button btnHistorialCompras;
    private Button btnLogout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar vistas
        tvTotalGastado = findViewById(R.id.tvTotalGastado);
        btnRegistrarCompra = findViewById(R.id.btnRegistrarCompra);
        btnHistorialCompras = findViewById(R.id.btnHistorialCompras);
        btnLogout = findViewById(R.id.btnLogout);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar botón para registrar compra
        btnRegistrarCompra.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegistrarCompraActivity.class);
            startActivity(intent);
        });

        // Configurar botón para historial de compras
        btnHistorialCompras.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistorialComprasActivity.class);
            startActivity(intent);
        });

        // Configurar botón de logout
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            // Evitar que el usuario vuelva atrás después de cerrar sesión
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Llamar a calcularTotalGastado cada vez que la actividad vuelve al primer plano
        calcularTotalGastado();
    }

    private void calcularTotalGastado() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String userId = currentUser.getUid();

        db.collection("compras")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Contar el número de requisiciones en lugar del total gastado
                    int totalRequisiciones = queryDocumentSnapshots.size();
                    String totalFormateado = "Total Requisiciones: " + totalRequisiciones;
                    tvTotalGastado.setText(totalFormateado);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al calcular las requisiciones", e);
                    Toast.makeText(HomeActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                });
    }
}
