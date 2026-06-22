package com.example.appcompras;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;

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

        configurarSwipeToDelete();
        cargarServidores();
    }

    private void configurarSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Servidor servidorAEliminar = listaServidores.get(position);
                String idDocumento = servidorAEliminar.getIdDocumento();

                if (idDocumento != null && !idDocumento.isEmpty()) {
                    db.collection("servidores").document(idDocumento)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                listaServidores.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(InventarioServidoresActivity.this, "Servidor eliminado correctamente", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al eliminar el servidor", e);
                                Toast.makeText(InventarioServidoresActivity.this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                adapter.notifyItemChanged(position); // Restaurar vista
                            });
                } else {
                    Toast.makeText(InventarioServidoresActivity.this, "Error: ID de servidor no válido", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvServidores);
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
