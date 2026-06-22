package com.example.appcompras;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InventarioServidoresActivity extends AppCompatActivity {

    private RecyclerView rvServidores;
    private FloatingActionButton fabExportarServidores;
    private ServidorAdapter adapter;
    private List<Servidor> listaServidores;
    private FirebaseFirestore db;
    private static final String TAG = "InventarioServidores";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario_servidores);

        rvServidores = findViewById(R.id.rvServidores);
        rvServidores.setLayoutManager(new LinearLayoutManager(this));

        fabExportarServidores = findViewById(R.id.fabExportarServidores);
        fabExportarServidores.setOnClickListener(v -> {
            ExportadorCSV.exportarServidores(InventarioServidoresActivity.this);
        });

        listaServidores = new ArrayList<>();
        adapter = new ServidorAdapter(listaServidores);
        rvServidores.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        cargarServidores();
    }

    private void cargarServidores() {
        db.collection("servidores")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaServidores.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Servidor servidor = document.toObject(Servidor.class);
                        listaServidores.add(servidor);
                    }
                    adapter.setServidores(listaServidores);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al cargar los servidores", e);
                    Toast.makeText(InventarioServidoresActivity.this, "Error al cargar el inventario", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarServidores(); // Refrescar la lista si volvemos a esta pantalla
    }
}
