package com.example.appcompras;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistorialComprasActivity extends AppCompatActivity {

    private RecyclerView rvCompras;
    private CompraAdapter adapter;
    private List<Compra> listaComprasOriginal;
    private List<Compra> listaComprasFiltrada;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private EditText etFiltroFechaCompra, etFiltroResponsable;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, "Permiso de almacenamiento requerido para generar PDF en esta versión de Android", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_compras);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        rvCompras = findViewById(R.id.rvCompras);
        rvCompras.setLayoutManager(new LinearLayoutManager(this));

        listaComprasOriginal = new ArrayList<>();
        listaComprasFiltrada = new ArrayList<>();
        adapter = new CompraAdapter(listaComprasFiltrada);
        rvCompras.setAdapter(adapter);

        etFiltroFechaCompra = findViewById(R.id.etFiltroFechaCompra);
        etFiltroResponsable = findViewById(R.id.etFiltroResponsable);

        TextWatcher filtroWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etFiltroFechaCompra.addTextChangedListener(filtroWatcher);
        etFiltroResponsable.addTextChangedListener(filtroWatcher);

        solicitarPermisoAlmacenamiento();
        configurarSwipeParaEliminar();
        cargarCompras();
    }

    private void solicitarPermisoAlmacenamiento() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    private void aplicarFiltros() {
        String filtroFecha = etFiltroFechaCompra.getText().toString().trim().toLowerCase();
        String filtroResp = etFiltroResponsable.getText().toString().trim().toLowerCase();

        listaComprasFiltrada.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Compra compra : listaComprasOriginal) {
            boolean coincideFecha = true;
            boolean coincideResp = true;

            if (!filtroFecha.isEmpty()) {
                if (compra.getFecha() != null) {
                    String date = sdf.format(compra.getFecha().toDate());
                    coincideFecha = date.contains(filtroFecha);
                } else {
                    coincideFecha = false;
                }
            }

            if (!filtroResp.isEmpty()) {
                if (compra.getResponsable() != null) {
                    coincideResp = compra.getResponsable().toLowerCase().contains(filtroResp);
                } else {
                    coincideResp = false;
                }
            }

            if (coincideFecha && coincideResp) {
                listaComprasFiltrada.add(compra);
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
                Compra compraAEliminar = listaComprasFiltrada.get(position);

                if (compraAEliminar.getIdDocumento() != null) {
                    db.collection("compras").document(compraAEliminar.getIdDocumento())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                // Cascada: eliminar productos asociados
                                db.collection("compras").document(compraAEliminar.getIdDocumento()).collection("productos")
                                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            doc.getReference().delete();
                                        }
                                    });

                                // Cascada: eliminar fotos en Storage
                                if (compraAEliminar.getFotosFacturas() != null) {
                                    for (String url : compraAEliminar.getFotosFacturas()) {
                                        try {
                                            StorageReference ref = storage.getReferenceFromUrl(url);
                                            ref.delete();
                                        } catch(Exception ignored) {}
                                    }
                                }

                                listaComprasOriginal.remove(compraAEliminar);
                                listaComprasFiltrada.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(HistorialComprasActivity.this, "Compra eliminada", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                adapter.notifyItemChanged(position);
                                Toast.makeText(HistorialComprasActivity.this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvCompras);
    }

    private void cargarCompras() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("compras")
                .whereEqualTo("userId", user.getUid())
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaComprasOriginal.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Compra compra = document.toObject(Compra.class);
                        compra.setIdDocumento(document.getId());
                        listaComprasOriginal.add(compra);
                    }
                    aplicarFiltros();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HistorialComprasActivity.this, "Error al cargar compras: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCompras();
    }
}
