package com.david.fixittecnic;

public class Tecnico {
    private Long id;
    private String nombre;
    private String email;
    private String especialidad;
    private String rol;
    private String telefono;
    private Double calificacion;
    private Boolean activo;

    // Getters para poder leer los datos en el móvil
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getEspecialidad() { return especialidad; }
    public String getRol() { return rol; }
    public String getTelefono() { return telefono; }
    public Double getCalificacion() { return calificacion; }
    public Boolean getActivo() { return activo; }
}