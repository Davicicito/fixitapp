package com.david.fixittecnic;

/**
 * Modelo de datos que representa un producto o repuesto del inventario en la aplicacion movil.
 * Contiene toda la informacion necesaria para que el operario identifique el material,
 * compruebe si hay existencias suficientes y registre su uso en las reparaciones.
 */
public class MaterialApi {
    private int id;
    private String nombre;
    private String unidad;
    private double precio;
    private int stock;

    /**
     * Recupera el identificador unico del material en la base de datos.
     * @return El numero de identificacion del producto.
     */
    public int getId() { return id; }

    /**
     * Obtiene el nombre comercial o la descripcion del repuesto.
     * @return El texto con el nombre del material.
     */
    public String getNombre() { return nombre; }

    /**
     * Obtiene la forma en la que se mide el material, como por ejemplo metros, kilos o unidades.
     * @return El texto que define la unidad de medida.
     */
    public String getUnidad() { return unidad; }

    /**
     * Consulta el coste economico por unidad de este material.
     * @return El valor numerico del precio.
     */
    public double getPrecio() { return precio; }

    /**
     * Indica cuanta cantidad queda disponible en el almacen central o en el vehiculo.
     * @return El numero de existencias actuales.
     */
    public int getStock() { return stock; }

    /**
     * Metodo especial que decide como se muestra el objeto cuando aparece en una lista desplegable.
     * En este caso devuelve directamente el nombre para que el tecnico pueda elegirlo facilmente.
     * * @return El nombre del material para su visualizacion en la interfaz.
     */
    @Override
    public String toString() {
        return nombre;
    }
}