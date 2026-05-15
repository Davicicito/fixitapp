package com.david.fixittecnic;

/**
 * Clase que representa el consumo de un material especifico durante una reparacion.
 * Se utiliza para registrar que pieza se ha utilizado y en que cantidad, permitiendo
 * que el servidor descuente estas unidades del inventario global de la empresa.
 */
public class MaterialGastado {
    private Long idMaterial;
    private double cantidad;

    /**
     * Constructor para crear un registro de gasto de material.
     * @param idMaterial El numero identificador unico de la pieza utilizada.
     * @param cantidad La cifra exacta de unidades o medida consumida en el trabajo.
     */
    public MaterialGastado(Long idMaterial, double cantidad) {
        this.idMaterial = idMaterial;
        this.cantidad = cantidad;
    }

    /**
     * Recupera el identificador del material que se ha gastado.
     * @return El numero de identificacion del producto.
     */
    public Long getIdMaterial() {
        return idMaterial;
    }

    /**
     * Define que material es el que se ha consumido en la intervencion tecnica.
     * @param idMaterial El numero identificador sacado del catalogo de materiales.
     */
    public void setIdMaterial(Long idMaterial) {
        this.idMaterial = idMaterial;
    }

    /**
     * Recupera el numero de unidades o la medida que el operario ha anotado.
     * @return La cantidad de producto utilizada.
     */
    public double getCantidad() {
        return cantidad;
    }

    /**
     * Establece cuanta cantidad de este repuesto se ha gastado durante la tarea.
     * @param cantidad El numero de unidades o medida gastada.
     */
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }
}