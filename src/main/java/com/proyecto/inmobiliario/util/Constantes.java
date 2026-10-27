package com.proyecto.inmobiliario.util;

import java.util.List;

public final class Constantes {

    public static final String PENDIENTE = "pendiente";
    public static final String ACEPTADO = "aceptado";
    public static final String CANCELADO = "cancelado";
    public static final List<String> ESTADOS = List.of(PENDIENTE, ACEPTADO, CANCELADO);

    public static final String VENDEDOR = "vendedor";
    public static final String COMPRADOR = "comprador";

    private Constantes() {
    }
}
