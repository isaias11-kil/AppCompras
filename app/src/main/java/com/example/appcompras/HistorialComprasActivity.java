package com.example.appcompras;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialComprasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCompras;
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

        compraList = new ArrayList<>();
        compraAdapter = new CompraAdapter(compraList);
        recyclerViewCompras.setAdapter(compraAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cargarCompras();
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
                        compraList.add(compra);
                    }
                    compraAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HistorialComprasActivity.this, "Error al cargar historial: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
