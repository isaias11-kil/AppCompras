package com.example.appcompras;

public class Servidor {
    private String idDocumento;
    private String tipoServidor;
    private String procesador;
    private String ram;
    private String almacenamiento;
    private String direccionIp;
    private String area;

    public Servidor() {
        // Constructor vacío requerido por Firestore
    }

    public Servidor(String idDocumento, String tipoServidor, String procesador, String ram, String almacenamiento, String direccionIp, String area) {
        this.idDocumento = idDocumento;
        this.tipoServidor = tipoServidor;
        this.procesador = procesador;
        this.ram = ram;
        this.almacenamiento = almacenamiento;
        this.direccionIp = direccionIp;
        this.area = area;
    }

    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getTipoServidor() {
        return tipoServidor;
    }

    public void setTipoServidor(String tipoServidor) {
        this.tipoServidor = tipoServidor;
    }

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getAlmacenamiento() {
        return almacenamiento;
    }

    public void setAlmacenamiento(String almacenamiento) {
        this.almacenamiento = almacenamiento;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
