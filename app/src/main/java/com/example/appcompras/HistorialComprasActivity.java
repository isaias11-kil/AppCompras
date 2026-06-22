package com.example.appcompras;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialComprasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCompras;
    private FloatingActionButton fabExportarCompras;
    private CompraAdapter compraAdapter;
    private List<Compra> compraList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_compras);

        recyclerViewCompras = findViewById(R.id.recyclerViewCompras);
        recyclerViewCompras.setLayoutManager(new LinearLayoutManager(this));

        fabExportarCompras = findViewById(R.id.fabExportarCompras);
        fabExportarCompras.setOnClickListener(v -> {
            ExportadorCSV.exportarRequisiciones(HistorialComprasActivity.this);
        });

        compraList = new ArrayList<>();
        compraAdapter = new CompraAdapter(compraList);
        recyclerViewCompras.setAdapter(compraAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        configurarSwipeToDelete();

        cargarCompras();
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
                Compra compraAEliminar = compraList.get(position);
                eliminarCompra(compraAEliminar, position);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewCompras);
    }

    private void eliminarCompra(Compra compra, int position) {
        String idDocumento = compra.getIdDocumento();
        String imageUrl = compra.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl).delete()
                    .addOnSuccessListener(aVoid -> eliminarDocumentoFirestore(idDocumento, position))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al eliminar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        compraAdapter.notifyItemChanged(position);
                    });
        } else {
            eliminarDocumentoFirestore(idDocumento, position);
        }
    }

    private void eliminarDocumentoFirestore(String idDocumento, int position) {
        db.collection("compras").document(idDocumento).delete()
                .addOnSuccessListener(aVoid -> {
                    compraList.remove(position);
                    compraAdapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Compra eliminada exitosamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar el documento: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    compraAdapter.notifyItemChanged(position);
                });
    }

    private void cargarCompras() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("compras")
                .whereEqualTo("userId", userId)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    compraList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Compra compra = document.toObject(Compra.class);
                        compra.setIdDocumento(document.getId());
                        compraList.add(compra);
                    }
                    compraAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HistorialComprasActivity.this, "Error al cargar historial: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
