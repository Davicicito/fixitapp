package com.david.fixittecnic;

/**
 * Modelo de datos que representa el perfil completo de un operario en el sistema.
 * Almacena la informacion personal, el cargo que ocupa en la empresa y su estado
 * actual para permitir que la aplicacion personalice la experiencia segun el usuario.
 */
public class Tecnico {
    private Long id;
    private String nombre;
    private String email;
    private String especialidad;
    private String rol;
    private String telefono;
    private Double calificacion;
    private Boolean activo;

    /**
     * Recupera el numero de identificador unico del trabajador.
     * @return El identificador oficial del tecnico.
     */
    public Long getId() { return id; }

    /**
     * Obtiene el nombre y los apellidos del empleado.
     * @return El texto con el nombre completo.
     */
    public String getNombre() { return nombre; }

    /**
     * Obtiene la direccion de correo utilizada para el acceso y las notificaciones.
     * @return El email del operario.
     */
    public String getEmail() { return email; }

    /**
     * Indica el area de conocimiento principal del tecnico como electricidad o fontaneria.
     * @return La especialidad tecnica asignada.
     */
    public String getEspecialidad() { return especialidad; }

    /**
     * Indica el nivel de permisos que tiene el usuario dentro de la plataforma.
     * @return El puesto o funcion que desempeña.
     */
    public String getRol() { return rol; }

    /**
     * Obtiene el numero de contacto oficial del trabajador.
     * @return El telefono de empresa.
     */
    public String getTelefono() { return telefono; }

    /**
     * Recupera la puntuacion media otorgada por los clientes tras finalizar los servicios.
     * @return La nota de valoracion del empleado.
     */
    public Double getCalificacion() { return calificacion; }

    /**
     * Comprueba si el tecnico se encuentra disponible y con la cuenta habilitada.
     * @return Verdadero si el usuario esta dado de alta actualmente.
     */
    public Boolean getActivo() { return activo; }
}