package com.example.appcompras;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

public class Insumo {
    private String idDocumento;
    private Timestamp fecha;
    private String producto;
    private double cantidad;
    private String unidadMedida;
    private String observaciones;
    private String userId;

    public Insumo() {
        // Constructor vacío requerido por Firestore
    }

    public Insumo(Timestamp fecha, String producto, double cantidad, String unidadMedida, String observaciones, String userId) {
        this.fecha = fecha;
        this.producto = producto;
        this.cantidad = cantidad;
        this.unidadMedida = unidadMedida;
        this.observaciones = observaciones;
        this.userId = userId;
    }

    @Exclude
    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
