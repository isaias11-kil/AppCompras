package com.example.appcompras;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegistrarCompraActivity extends AppCompatActivity {

    private EditText etCantidad;
    private EditText etDescripcion;
    private ImageView ivPreviewFoto;
    private Button btnSeleccionarFoto;
    private Button btnGuardarCompra;
    private ProgressBar progressBar;

    private Uri imageUri;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_compra);

        etCantidad = findViewById(R.id.etCantidad);
        etDescripcion = findViewById(R.id.etDescripcion);
        ivPreviewFoto = findViewById(R.id.ivPreviewFoto);
        btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto);
        btnGuardarCompra = findViewById(R.id.btnGuardarCompra);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageUri = uri;
                ivPreviewFoto.setImageURI(imageUri);
            }
        });

        btnSeleccionarFoto.setOnClickListener(v -> mGetContent.launch("image/*"));

        btnGuardarCompra.setOnClickListener(v -> guardarCompra());
    }

    private void guardarCompra() {
        String cantidadStr = etCantidad.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (TextUtils.isEmpty(cantidadStr)) {
            etCantidad.setError("La cantidad es requerida");
            return;
        }

        if (TextUtils.isEmpty(descripcion)) {
            etDescripcion.setError("La descripción es requerida");
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Por favor selecciona una foto de la factura", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        setLoadingState(true);

        // Upload image to Firebase Storage
        String imageFileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference fileReference = storageReference.child("facturas/" + imageFileName);

        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                guardarDatosFirestore(userId, cantidadStr, descripcion, downloadUrl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                setLoadingState(false);
                                Toast.makeText(RegistrarCompraActivity.this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        setLoadingState(false);
                        Toast.makeText(RegistrarCompraActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarDatosFirestore(String userId, String cantidad, String descripcion, String imageUrl) {
        Map<String, Object> compra = new HashMap<>();
        compra.put("userId", userId);
        compra.put("cantidad", cantidad);
        compra.put("descripcion", descripcion);
        compra.put("imageUrl", imageUrl);
        compra.put("fecha", FieldValue.serverTimestamp());

        db.collection("compras")
                .add(compra)
                .addOnSuccessListener(documentReference -> {
                    setLoadingState(false);
                    Toast.makeText(RegistrarCompraActivity.this, "Compra registrada exitosamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar la actividad
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false);
                    Toast.makeText(RegistrarCompraActivity.this, "Error al guardar la compra", Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSeleccionarFoto.setEnabled(false);
            btnGuardarCompra.setEnabled(false);
            etCantidad.setEnabled(false);
            etDescripcion.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSeleccionarFoto.setEnabled(true);
            btnGuardarCompra.setEnabled(true);
            etCantidad.setEnabled(true);
            etDescripcion.setEnabled(true);
        }
    }
}
