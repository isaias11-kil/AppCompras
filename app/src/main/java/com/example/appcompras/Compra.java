package com.example.appcompras;

import com.google.firebase.Timestamp;

public class Compra {
    private String descripcion;
    private Double total;
    private Timestamp fecha;
    private String imageUrl;
    private String userId;

    public Compra() {
        // Constructor vacío requerido por Firestore
    }

    public Compra(String descripcion, Double total, Timestamp fecha, String imageUrl, String userId) {
        this.descripcion = descripcion;
        this.total = total;
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

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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
}
