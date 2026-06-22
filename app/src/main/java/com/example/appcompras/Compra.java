package com.example.appcompras;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import java.util.List;

public class Compra {
    private String idDocumento;
    private Timestamp fecha;
    private String responsable;
    private String actividad;
    private double montoEntregado;
    private double totalGastado;
    private double diferencia;
    private int cantidadFacturas;
    private String observaciones;
    private String userId;
    private List<String> fotosFacturas;

    public Compra() {
        // Constructor vacío requerido por Firestore
    }

    public Compra(Timestamp fecha, String responsable, String actividad, double montoEntregado, double totalGastado, double diferencia, int cantidadFacturas, String observaciones, String userId, List<String> fotosFacturas) {
        this.fecha = fecha;
        this.responsable = responsable;
        this.actividad = actividad;
        this.montoEntregado = montoEntregado;
        this.totalGastado = totalGastado;
        this.diferencia = diferencia;
        this.cantidadFacturas = cantidadFacturas;
        this.observaciones = observaciones;
        this.userId = userId;
        this.fotosFacturas = fotosFacturas;
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

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public double getMontoEntregado() {
        return montoEntregado;
    }

    public void setMontoEntregado(double montoEntregado) {
        this.montoEntregado = montoEntregado;
    }

    public double getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(double totalGastado) {
        this.totalGastado = totalGastado;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public int getCantidadFacturas() {
        return cantidadFacturas;
    }

    public void setCantidadFacturas(int cantidadFacturas) {
        this.cantidadFacturas = cantidadFacturas;
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

    public List<String> getFotosFacturas() {
        return fotosFacturas;
    }

    public void setFotosFacturas(List<String> fotosFacturas) {
        this.fotosFacturas = fotosFacturas;
    }
}
