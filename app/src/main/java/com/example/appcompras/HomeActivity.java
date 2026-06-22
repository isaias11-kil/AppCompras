package com.example.appcompras;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private MaterialButton btnCerrarSesion;
    private TextView tvTotalComprasMes, tvTotalGastadoMes, tvTotalInsumos, tvUltimaCompra;

    // Cards
    private MaterialCardView cardNuevaCompra, cardHistorialCompras, cardNuevoInsumo, cardHistorialInsumos, cardReportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        tvTotalComprasMes = findViewById(R.id.tvTotalComprasMes);
        tvTotalGastadoMes = findViewById(R.id.tvTotalGastadoMes);
        tvTotalInsumos = findViewById(R.id.tvTotalInsumos);
        tvUltimaCompra = findViewById(R.id.tvUltimaCompra);

        cardNuevaCompra = findViewById(R.id.cardNuevaCompra);
        cardHistorialCompras = findViewById(R.id.cardHistorialCompras);
        cardNuevoInsumo = findViewById(R.id.cardNuevoInsumo);
        cardHistorialInsumos = findViewById(R.id.cardHistorialInsumos);
        cardReportes = findViewById(R.id.cardReportes);

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        cardNuevaCompra.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RegistrarCompraActivity.class));
        });

        cardHistorialCompras.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, HistorialComprasActivity.class));
        });

        cardNuevoInsumo.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, RegistrarInsumoActivity.class));
        });

        cardHistorialInsumos.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, HistorialInsumosActivity.class));
        });

        cardReportes.setOnClickListener(v -> {
            ExportadorCSV.exportarCompras(this);
        });

        cargarResumen();
    }

    private void cargarResumen() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfMonth = cal.getTime();

        db.collection("compras")
            .whereEqualTo("userId", userId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                int totalCompras = 0;
                double totalGastado = 0;
                boolean isFirst = true;

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Compra c = document.toObject(Compra.class);

                    if (isFirst) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String dateStr = c.getFecha() != null ? sdf.format(c.getFecha().toDate()) : "N/A";
                        tvUltimaCompra.setText("Última Compra: " + dateStr + " - $" + c.getTotalGastado());
                        isFirst = false;
                    }

                    if (c.getFecha() != null && !c.getFecha().toDate().before(startOfMonth)) {
                        totalCompras++;
                        totalGastado += c.getTotalGastado();
                    }
                }

                if (totalCompras == 0 && isFirst) tvUltimaCompra.setText("Última Compra: N/A");

                tvTotalComprasMes.setText("Total Compras del Mes: " + totalCompras);
                tvTotalGastadoMes.setText(String.format("Gastado en el Mes: $%.2f", totalGastado));
            });

        db.collection("insumos")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                tvTotalInsumos.setText("Total Insumos Registrados: " + queryDocumentSnapshots.size());
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarResumen();
    }

    private void cerrarSesion() {
        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
