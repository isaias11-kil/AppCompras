package com.example.appcompras;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfGenerator {

    public static void generarPdfLiquidacion(Context context, Compra compra, List<Producto> productos) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText("LIQUIDACIÓN DE FONDOS", 180, 50, paint);

        paint.setTextSize(14);
        int y = 100;
        canvas.drawText("Responsable: " + (compra.getResponsable() != null ? compra.getResponsable() : ""), 50, y, paint);
        y += 20;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fecha = compra.getFecha() != null ? sdf.format(compra.getFecha().toDate()) : "";
        canvas.drawText("Fecha: " + fecha, 50, y, paint);
        y += 20;
        canvas.drawText("Actividad: " + (compra.getActividad() != null ? compra.getActividad() : ""), 50, y, paint);
        y += 40;

        // Table Header
        paint.setFakeBoldText(true);
        canvas.drawText("Producto", 50, y, paint);
        canvas.drawText("Cant.", 250, y, paint);
        canvas.drawText("P. Unit", 350, y, paint);
        canvas.drawText("Total", 450, y, paint);
        paint.setFakeBoldText(false);
        y += 20;

        canvas.drawLine(50, y, 500, y, paint);
        y += 20;

        if (productos != null) {
            for (Producto p : productos) {
                canvas.drawText(p.getDescripcion() != null ? p.getDescripcion() : "", 50, y, paint);
                canvas.drawText(String.valueOf(p.getCantidad()), 250, y, paint);
                canvas.drawText(String.valueOf(p.getPrecioUnitario()), 350, y, paint);
                canvas.drawText(String.valueOf(p.getTotal()), 450, y, paint);
                y += 20;
            }
        }

        y += 20;
        paint.setFakeBoldText(true);
        canvas.drawText("Total Gastado: " + compra.getTotalGastado(), 350, y, paint);
        y += 20;
        canvas.drawText("Monto Entregado: " + compra.getMontoEntregado(), 350, y, paint);
        y += 20;
        canvas.drawText("Diferencia: " + compra.getDiferencia(), 350, y, paint);
        paint.setFakeBoldText(false);

        y += 40;
        canvas.drawText("Observaciones: " + (compra.getObservaciones() != null ? compra.getObservaciones() : ""), 50, y, paint);

        document.finishPage(page);

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!directory.exists()) {
            boolean isCreated = directory.mkdirs();
            if (!isCreated) {
                 Toast.makeText(context, "Error al crear directorio para PDF", Toast.LENGTH_SHORT).show();
                 return;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File file = new File(directory, "Liquidacion_" + timeStamp + ".pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF guardado en Documentos: " + file.getName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al guardar PDF. Verifique permisos.", Toast.LENGTH_SHORT).show();
        } finally {
            document.close();
        }
    }

    public static void generarPdfInsumos(Context context, List<Insumo> insumos) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        canvas.drawText("CONTROL DE INSUMOS", 180, 50, paint);

        paint.setTextSize(14);
        int y = 100;

        // Table Header
        paint.setFakeBoldText(true);
        canvas.drawText("Fecha", 50, y, paint);
        canvas.drawText("Producto", 150, y, paint);
        canvas.drawText("Cant.", 300, y, paint);
        canvas.drawText("Unidad", 380, y, paint);
        canvas.drawText("Obs.", 460, y, paint);
        paint.setFakeBoldText(false);
        y += 20;

        canvas.drawLine(50, y, 550, y, paint);
        y += 20;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());

        if (insumos != null) {
            for (Insumo i : insumos) {
                String fecha = i.getFecha() != null ? sdf.format(i.getFecha().toDate()) : "";
                canvas.drawText(fecha, 50, y, paint);
                canvas.drawText(i.getProducto() != null ? i.getProducto() : "", 150, y, paint);
                canvas.drawText(String.valueOf(i.getCantidad()), 300, y, paint);
                canvas.drawText(i.getUnidadMedida() != null ? i.getUnidadMedida() : "", 380, y, paint);

                String obs = i.getObservaciones() != null ? i.getObservaciones() : "";
                if (obs.length() > 15) {
                    obs = obs.substring(0, 12) + "...";
                }
                canvas.drawText(obs, 460, y, paint);
                y += 20;

                if (y > 800) {
                     document.finishPage(page);
                     pageInfo = new PdfDocument.PageInfo.Builder(595, 842, document.getPages().size() + 1).create();
                     page = document.startPage(pageInfo);
                     canvas = page.getCanvas();
                     y = 50;
                }
            }
        }

        document.finishPage(page);

        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!directory.exists()) {
             boolean isCreated = directory.mkdirs();
             if (!isCreated) {
                 Toast.makeText(context, "Error al crear directorio para PDF", Toast.LENGTH_SHORT).show();
                 return;
             }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File file = new File(directory, "Insumos_" + timeStamp + ".pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF guardado en Documentos: " + file.getName(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al guardar PDF. Verifique permisos.", Toast.LENGTH_SHORT).show();
        } finally {
             document.close();
        }
    }
}
