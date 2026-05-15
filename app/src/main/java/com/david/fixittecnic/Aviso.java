package com.david.fixittecnic;

/**
 * Modelo de datos que representa un parte de trabajo dentro de la aplicacion movil.
 * Esta clase almacena toda la informacion relevante de la averia incluyendo los datos
 * del cliente y la especialidad tecnica necesaria para la reparacion.
 */
public class Aviso {
    private Long id;
    private String descripcion;
    private String estado;
    private String prioridad;

    private String fechaCreacion;

    private ClienteDTO cliente;
    private CategoriaDTO categoria;

    // --- GETTERS PRINCIPALES ---
    /**
     * Recupera el numero de identificacion unico del aviso.
     * @return El identificador del parte de trabajo.
     */
    public Long getId() { return id; }

    /**
     * Obtiene el texto con la explicacion del problema detectado.
     * @return La descripcion de la averia.
     */
    public String getDescripcion() { return descripcion; }

    /**
     * Consulta si el trabajo esta pendiente o en proceso.
     * @return El nombre del estado actual.
     */
    public String getEstado() { return estado; }

    /**
     * Indica la urgencia del aviso segun el criterio del jefe de equipo.
     * @return El nivel de prioridad asignado.
     */
    public String getPrioridad() { return prioridad; }

    /**
     * Obtiene el dia y la hora en la que se registro el aviso.
     * @return La fecha de creacion del parte.
     */
    public String getFechaCreacion() { return fechaCreacion; }

    /**
     * Accede a la informacion personal y de contacto del cliente.
     * @return El objeto con los datos del usuario.
     */
    public ClienteDTO getCliente() { return cliente; }

    /**
     * Accede a la especialidad tecnica requerida para este trabajo.
     * @return El objeto con el nombre de la categoria.
     */
    public CategoriaDTO getCategoria() { return categoria; }

    /**
     * Clase interna que agrupa los datos de contacto de la persona que solicita la reparacion.
     */
    public static class ClienteDTO {
        private String nombre;
        private String direccion;
        private String telefono;

        /**
         * Obtiene el nombre completo del cliente o la empresa.
         * @return El nombre del contacto.
         */
        public String getNombre() { return nombre; }

        /**
         * Obtiene el lugar exacto donde el tecnico debe realizar el trabajo.
         * @return La direccion postal del cliente.
         */
        public String getDireccion() { return direccion; }

        /**
         * Obtiene el numero de contacto para avisar al cliente antes de llegar.
         * @return El telefono del usuario.
         */
        public String getTelefono() { return telefono; }
    }

    /**
     * Clase interna que define la especialidad tecnica del aviso.
     */
    public static class CategoriaDTO {
        private String nombre;

        /**
         * Obtiene el nombre de la especialidad como fontaneria o electricidad.
         * @return El nombre de la categoria.
         */
        public String getNombre() { return nombre; }
    }
}