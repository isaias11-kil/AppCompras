package com.example.appcompras;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportadorCSV {

    public static void exportarCompras(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(context, "Debe iniciar sesión para exportar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("compras")
            .whereEqualTo("userId", user.getUid())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                StringBuilder csvData = new StringBuilder();
                csvData.append("Actividad,Responsable,Total Gastado,Monto Entregado,Observaciones\n");

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Compra compra = document.toObject(Compra.class);
                    String act = escapeCsv(compra.getActividad());
                    String resp = escapeCsv(compra.getResponsable());
                    String total = String.valueOf(compra.getTotalGastado());
                    String monto = String.valueOf(compra.getMontoEntregado());
                    String obs = escapeCsv(compra.getObservaciones());

                    csvData.append(act).append(",")
                           .append(resp).append(",")
                           .append(total).append(",")
                           .append(monto).append(",")
                           .append(obs).append("\n");
                }

                guardarYCompartir(context, "compras.csv", csvData.toString());
            })
            .addOnFailureListener(e -> Toast.makeText(context, "Error al generar CSV de compras", Toast.LENGTH_SHORT).show());
    }

    public static void exportarInsumos(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(context, "Debe iniciar sesión para exportar", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("insumos")
            .whereEqualTo("userId", user.getUid())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                StringBuilder csvData = new StringBuilder();
                csvData.append("Producto,Cantidad,Unidad,Observaciones\n");

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Insumo insumo = document.toObject(Insumo.class);
                    String prod = escapeCsv(insumo.getProducto());
                    String cant = String.valueOf(insumo.getCantidad());
                    String unidad = escapeCsv(insumo.getUnidadMedida());
                    String obs = escapeCsv(insumo.getObservaciones());

                    csvData.append(prod).append(",")
                           .append(cant).append(",")
                           .append(unidad).append(",")
                           .append(obs).append("\n");
                }

                guardarYCompartir(context, "insumos.csv", csvData.toString());
            })
            .addOnFailureListener(e -> Toast.makeText(context, "Error al generar CSV de insumos", Toast.LENGTH_SHORT).show());
    }

    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static void guardarYCompartir(Context context, String filename, String data) {
        try {
            File tempFile = new File(context.getCacheDir(), filename);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(data.getBytes());
            fos.close();

            Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", tempFile);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Exportación: " + filename);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(intent, "Compartir CSV..."));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
        }
    }
}
