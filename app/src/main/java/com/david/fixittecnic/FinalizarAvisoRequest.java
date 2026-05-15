package com.david.fixittecnic;

import java.util.List;

/**
 * Clase que agrupa toda la informacion final de una reparacion para enviarla al servidor.
 * Este objeto funciona como un contenedor que transporta las evidencias del trabajo,
 * como la fotografia del arreglo, la firma de conformidad del cliente y el listado
 * de piezas que se han retirado del stock del camion.
 */
public class FinalizarAvisoRequest {
    private String estado;
    private String observaciones;
    private String fotoBase64;
    private String firmaBase64;
    private Integer valoracionCliente;
    private List<MaterialGastado> materialesUsados;

    /**
     * Constructor principal para crear la peticion de cierre de trabajo.
     * Reune todos los campos obligatorios y opcionales que el tecnico ha rellenado
     * en el formulario de finalizacion desde la pantalla del movil.
     *
     * @param estado Texto que indica que el trabajo ha sido completado.
     * @param observaciones Comentarios adicionales del operario sobre el resultado del arreglo.
     * @param fotoBase64 Imagen del trabajo terminado convertida a una cadena de texto larga.
     * @param firmaBase64 Dibujo de la firma del cliente capturada en la pantalla tactil.
     * @param valoracionCliente Puntuacion de satisfaccion que el cliente otorga al servicio.
     * @param materialesUsados Lista detallada de los repuestos consumidos durante la tarea.
     */
    public FinalizarAvisoRequest(String estado, String observaciones, String fotoBase64, String firmaBase64, Integer valoracionCliente, List<MaterialGastado> materialesUsados) {
        this.estado = estado;
        this.observaciones = observaciones;
        this.fotoBase64 = fotoBase64;
        this.firmaBase64 = firmaBase64;
        this.valoracionCliente = valoracionCliente;
        this.materialesUsados = materialesUsados;
    }
}