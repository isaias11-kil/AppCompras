package com.example.appcompras;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistorialInsumosActivity extends AppCompatActivity {

    private RecyclerView rvInsumos;
    private InsumoAdapter adapter;
    private List<Insumo> listaInsumosOriginal;
    private List<Insumo> listaInsumosFiltrada;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FloatingActionButton fabExportarInsumos;
    private MaterialButton btnGenerarPdfInsumos;
    private EditText etFiltroFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_insumos);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rvInsumos = findViewById(R.id.rvInsumos);
        rvInsumos.setLayoutManager(new LinearLayoutManager(this));

        listaInsumosOriginal = new ArrayList<>();
        listaInsumosFiltrada = new ArrayList<>();
        adapter = new InsumoAdapter(listaInsumosFiltrada);
        rvInsumos.setAdapter(adapter);

        etFiltroFecha = findViewById(R.id.etFiltroFecha);
        etFiltroFecha.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarPorFecha(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        fabExportarInsumos = findViewById(R.id.fabExportarInsumos);
        fabExportarInsumos.setOnClickListener(v -> {
            ExportadorCSV.exportarInsumos(this);
        });

        btnGenerarPdfInsumos = findViewById(R.id.btnGenerarPdfInsumos);
        btnGenerarPdfInsumos.setOnClickListener(v -> {
             if (listaInsumosFiltrada.isEmpty()) {
                 Toast.makeText(this, "No hay insumos para generar PDF", Toast.LENGTH_SHORT).show();
                 return;
             }
             PdfGenerator.generarPdfInsumos(this, listaInsumosFiltrada);
        });

        configurarSwipeParaEliminar();
        cargarInsumos();
    }

    private void filtrarPorFecha(String fechaStr) {
        listaInsumosFiltrada.clear();
        if (fechaStr.isEmpty()) {
            listaInsumosFiltrada.addAll(listaInsumosOriginal);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            for (Insumo insumo : listaInsumosOriginal) {
                if (insumo.getFecha() != null) {
                    String date = sdf.format(insumo.getFecha().toDate());
                    if (date.contains(fechaStr)) {
                        listaInsumosFiltrada.add(insumo);
                    }
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void configurarSwipeParaEliminar() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Insumo insumoAEliminar = listaInsumosFiltrada.get(position);

                if (insumoAEliminar.getIdDocumento() != null) {
                    db.collection("insumos").document(insumoAEliminar.getIdDocumento())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                listaInsumosOriginal.remove(insumoAEliminar);
                                listaInsumosFiltrada.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(HistorialInsumosActivity.this, "Insumo eliminado", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                adapter.notifyItemChanged(position);
                                Toast.makeText(HistorialInsumosActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvInsumos);
    }

    private void cargarInsumos() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("insumos")
                .whereEqualTo("userId", user.getUid())
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaInsumosOriginal.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Insumo insumo = document.toObject(Insumo.class);
                        insumo.setIdDocumento(document.getId());
                        listaInsumosOriginal.add(insumo);
                    }
                    filtrarPorFecha(etFiltroFecha.getText().toString());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HistorialInsumosActivity.this, "Error al cargar insumos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarInsumos();
    }
}
