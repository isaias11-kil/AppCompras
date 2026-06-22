package com.example.appcompras;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Compra {
    private String idDocumento;
    private String descripcion;
    private String cantidad;
    private Timestamp fecha;
    private String imageUrl;
    private String userId;

    public Compra() {
        // Constructor vacío requerido por Firestore
    }

    public Compra(String descripcion, String cantidad, Timestamp fecha, String imageUrl, String userId) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }
}
