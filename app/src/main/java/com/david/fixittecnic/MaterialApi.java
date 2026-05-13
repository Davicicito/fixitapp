package com.david.fixittecnic;

public class MaterialApi {
    private int id;
    private String nombre;
    private String unidad;
    private double precio;

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUnidad() { return unidad; }
    public double getPrecio() { return precio; }

    @Override
    public String toString() {
        return nombre;
    }
}