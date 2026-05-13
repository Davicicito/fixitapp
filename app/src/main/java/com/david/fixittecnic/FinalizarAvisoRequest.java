package com.david.fixittecnic;

import java.util.List;

public class FinalizarAvisoRequest {
    private String estado;
    private String observaciones;
    private String fotoBase64;
    private String firmaBase64;
    private Integer valoracionCliente;
    private List<MaterialGastado> materialesUsados;

    public FinalizarAvisoRequest(String estado, String observaciones, String fotoBase64, String firmaBase64, Integer valoracionCliente, List<MaterialGastado> materialesUsados) {
        this.estado = estado;
        this.observaciones = observaciones;
        this.fotoBase64 = fotoBase64;
        this.firmaBase64 = firmaBase64;
        this.valoracionCliente = valoracionCliente;
        this.materialesUsados = materialesUsados;
    }
}