package com.david.fixittecnic;

public class Aviso {
    private Long id;
    private String descripcion;
    private String estado;
    private String prioridad;

    // Spring Boot envía el LocalDateTime como un texto
    private String fechaCreacion;

    // Recogemos los objetos anidados
    private ClienteDTO cliente;
    private CategoriaDTO categoria;

    // --- GETTERS PRINCIPALES ---
    public Long getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public String getEstado() { return estado; }
    public String getPrioridad() { return prioridad; }
    public String getFechaCreacion() { return fechaCreacion; }
    public ClienteDTO getCliente() { return cliente; }
    public CategoriaDTO getCategoria() { return categoria; }

    // --- SUB-MOLDES (Data Transfer Objects) ---
    public static class ClienteDTO {
        private String nombre;
        private String direccion;
        private String telefono; // 🔥 AÑADIDO EL TELÉFONO AQUÍ 🔥

        public String getNombre() { return nombre; }
        public String getDireccion() { return direccion; }
        public String getTelefono() { return telefono; } // 🔥 Y SU GETTER AQUÍ 🔥
    }

    public static class CategoriaDTO {
        private String nombre;
        public String getNombre() { return nombre; }
    }
}