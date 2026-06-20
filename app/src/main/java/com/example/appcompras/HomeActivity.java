package com.example.appcompras;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProductos;
    private ProductoAdapter productoAdapter;
    private List<Producto> productoList;
    private FirebaseFirestore db;
    private Button btnRegistrarCompra;
    private Button btnLogout;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inicializar vistas
        recyclerViewProductos = findViewById(R.id.recyclerViewProductos);
        btnRegistrarCompra = findViewById(R.id.btnRegistrarCompra);
        btnLogout = findViewById(R.id.btnLogout);

        // Configurar RecyclerView
        recyclerViewProductos.setLayoutManager(new LinearLayoutManager(this));
        productoList = new ArrayList<>();
        productoAdapter = new ProductoAdapter(productoList);
        recyclerViewProductos.setAdapter(productoAdapter);

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance();

        // Cargar datos
        cargarProductos();

        // Configurar botón para registrar compra
        btnRegistrarCompra.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegistrarCompraActivity.class);
            startActivity(intent);
        });

        // Configurar botón de logout
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            // Evitar que el usuario vuelva atrás después de cerrar sesión
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void cargarProductos() {
        db.collection("productos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productoList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Producto producto = document.toObject(Producto.class);
                                producto.setId(document.getId()); // Asignar el ID del documento
                                productoList.add(producto);
                            } catch (Exception e) {
                                Log.e(TAG, "Error al parsear el producto", e);
                            }
                        }
                        productoAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error obteniendo documentos: ", task.getException());
                        Toast.makeText(HomeActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
