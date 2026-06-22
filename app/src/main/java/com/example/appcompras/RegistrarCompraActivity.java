package com.example.appcompras;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RegistrarCompraActivity extends AppCompatActivity {

    private EditText etActividad, etResponsable, etMontoEntregado, etObservaciones;
    private EditText etDescProducto, etCantProducto, etPrecioProducto;
    private Button btnGuardarCompra, btnAgregarFoto, btnTomarFoto, btnAgregarProducto;
    private TextView tvTotalCalculado, tvFotosAgregadas;
    private RecyclerView rvProductosNuevos;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;

    private List<String> fotosUrls = new ArrayList<>();
    private List<Producto> listaProductos = new ArrayList<>();
    private ProductoAdapter productoAdapter;

    private double totalGastadoCalculado = 0.0;
    private double montoEntregadoActual = 0.0;

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    subirImagenAFirebase(uri);
                }
            });

    private final ActivityResultLauncher<Intent> mTakePicture = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    subirBitmapAFirebase(imageBitmap);
                }
            });

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    abrirCamara();
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_compra);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        etActividad = findViewById(R.id.etActividad);
        etResponsable = findViewById(R.id.etResponsable);
        etMontoEntregado = findViewById(R.id.etMontoEntregado);
        etObservaciones = findViewById(R.id.etObservaciones);

        etDescProducto = findViewById(R.id.etDescProducto);
        etCantProducto = findViewById(R.id.etCantProducto);
        etPrecioProducto = findViewById(R.id.etPrecioProducto);

        btnGuardarCompra = findViewById(R.id.btnGuardarCompra);
        btnAgregarFoto = findViewById(R.id.btnAgregarFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto);

        tvTotalCalculado = findViewById(R.id.tvTotalCalculado);
        tvFotosAgregadas = findViewById(R.id.tvFotosAgregadas);

        rvProductosNuevos = findViewById(R.id.rvProductosNuevos);
        rvProductosNuevos.setLayoutManager(new LinearLayoutManager(this));
        productoAdapter = new ProductoAdapter(listaProductos);
        rvProductosNuevos.setAdapter(productoAdapter);

        etMontoEntregado.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                try {
                    montoEntregadoActual = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    montoEntregadoActual = 0.0;
                }
                actualizarTotales();
            }
        });

        btnAgregarProducto.setOnClickListener(v -> agregarProductoLocal());
        btnGuardarCompra.setOnClickListener(v -> guardarCompra());
        btnAgregarFoto.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnTomarFoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            mTakePicture.launch(takePictureIntent);
        }
    }

    private void agregarProductoLocal() {
        String desc = etDescProducto.getText().toString().trim();
        String cantStr = etCantProducto.getText().toString().trim();
        String precioStr = etPrecioProducto.getText().toString().trim();

        if (TextUtils.isEmpty(desc) || TextUtils.isEmpty(cantStr) || TextUtils.isEmpty(precioStr)) {
            Toast.makeText(this, "Complete los datos del producto", Toast.LENGTH_SHORT).show();
            return;
        }

        int cant = Integer.parseInt(cantStr);
        double precio = Double.parseDouble(precioStr);
        double total = cant * precio;

        Producto p = new Producto(desc, cant, precio, total);
        listaProductos.add(p);
        productoAdapter.notifyDataSetChanged();

        totalGastadoCalculado += total;
        actualizarTotales();

        etDescProducto.setText("");
        etCantProducto.setText("");
        etPrecioProducto.setText("");
    }

    private void actualizarTotales() {
        double dif = montoEntregadoActual - totalGastadoCalculado;
        tvTotalCalculado.setText(String.format("Total Gastado: $%.2f | Diferencia: $%.2f", totalGastadoCalculado, dif));
    }

    private void subirImagenAFirebase(Uri uri) {
        btnGuardarCompra.setEnabled(false);
        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show();
        StorageReference imageRef = storage.getReference().child("facturas/" + UUID.randomUUID().toString());
        imageRef.putFile(uri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
            fotosUrls.add(downloadUri.toString());
            tvFotosAgregadas.setText("Fotos agregadas: " + fotosUrls.size());
            btnGuardarCompra.setEnabled(true);
        })).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al subir", Toast.LENGTH_SHORT).show();
            btnGuardarCompra.setEnabled(true);
        });
    }

    private void subirBitmapAFirebase(Bitmap bitmap) {
        btnGuardarCompra.setEnabled(false);
        Toast.makeText(this, "Subiendo foto...", Toast.LENGTH_SHORT).show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference imageRef = storage.getReference().child("facturas/" + UUID.randomUUID().toString() + ".jpg");
        imageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            fotosUrls.add(uri.toString());
            tvFotosAgregadas.setText("Fotos agregadas: " + fotosUrls.size());
            btnGuardarCompra.setEnabled(true);
        })).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al subir", Toast.LENGTH_SHORT).show();
            btnGuardarCompra.setEnabled(true);
        });
    }

    private void guardarCompra() {
        String actividad = etActividad.getText().toString().trim();
        String responsable = etResponsable.getText().toString().trim();
        String observaciones = etObservaciones.getText().toString().trim();

        if (TextUtils.isEmpty(actividad) || TextUtils.isEmpty(responsable)) {
            Toast.makeText(this, "Por favor complete actividad y responsable", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        btnGuardarCompra.setEnabled(false);

        String idDocumento = db.collection("compras").document().getId();
        Timestamp fecha = new Timestamp(new Date());
        double diferencia = montoEntregadoActual - totalGastadoCalculado;

        Compra compra = new Compra(fecha, responsable, actividad, montoEntregadoActual, totalGastadoCalculado, diferencia, fotosUrls.size(), observaciones, user.getUid(), fotosUrls);

        db.collection("compras").document(idDocumento).set(compra)
                .addOnSuccessListener(aVoid -> {
                    // Guardar subcoleccion de productos
                    for (Producto p : listaProductos) {
                        db.collection("compras").document(idDocumento).collection("productos").add(p);
                    }
                    Toast.makeText(RegistrarCompraActivity.this, "Compra guardada correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegistrarCompraActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnGuardarCompra.setEnabled(true);
                });
    }
}
