package com.david.fixittecnic;

public class MaterialGastado {
    private Long idMaterial;
    private double cantidad;

    public MaterialGastado(Long idMaterial, double cantidad) {
        this.idMaterial = idMaterial;
        this.cantidad = cantidad;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}