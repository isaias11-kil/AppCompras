package com.example.appcompras;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExportadorCSV {

    public static void exportarRequisiciones(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Generando CSV...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("compras").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder csvData = new StringBuilder();
                // Encabezados
                csvData.append("Cantidad,Descripción,Fecha\n");

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Compra compra = document.toObject(Compra.class);
                    String cantidad = escapeCsv(compra.getCantidad());
                    String descripcion = escapeCsv(compra.getDescripcion());
                    String fechaStr = "";
                    if (compra.getFecha() != null) {
                        Date date = compra.getFecha().toDate();
                        fechaStr = escapeCsv(sdf.format(date));
                    }
                    csvData.append(cantidad).append(",").append(descripcion).append(",").append(fechaStr).append("\n");
                }

                compartirCSV(context, csvData.toString(), "requisiciones.csv", progressDialog);

            } else {
                progressDialog.dismiss();
                Toast.makeText(context, "Error al obtener requisiciones.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void exportarServidores(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Generando CSV...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("servidores").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder csvData = new StringBuilder();
                // Encabezados
                csvData.append("Tipo,Procesador,RAM,Almacenamiento,IP,Área\n");

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Servidor servidor = document.toObject(Servidor.class);
                    String tipo = escapeCsv(servidor.getTipoServidor());
                    String procesador = escapeCsv(servidor.getProcesador());
                    String ram = escapeCsv(servidor.getRam());
                    String almacenamiento = escapeCsv(servidor.getAlmacenamiento());
                    String ip = escapeCsv(servidor.getDireccionIp());
                    String area = escapeCsv(servidor.getArea());

                    csvData.append(tipo).append(",")
                            .append(procesador).append(",")
                            .append(ram).append(",")
                            .append(almacenamiento).append(",")
                            .append(ip).append(",")
                            .append(area).append("\n");
                }

                compartirCSV(context, csvData.toString(), "servidores.csv", progressDialog);

            } else {
                progressDialog.dismiss();
                Toast.makeText(context, "Error al obtener servidores.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }

    private static void compartirCSV(Context context, String data, String fileName, ProgressDialog progressDialog) {
        try {
            File cacheDir = context.getCacheDir();
            File file = new File(cacheDir, fileName);
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.flush();
            writer.close();

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            progressDialog.dismiss();
            context.startActivity(Intent.createChooser(intent, "Compartir CSV usando"));
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(context, "Error al generar el archivo.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
