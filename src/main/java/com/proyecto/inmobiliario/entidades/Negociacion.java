package com.proyecto.inmobiliario.entidades;

import static com.proyecto.inmobiliario.util.Constantes.VENDEDOR;

import java.math.BigDecimal;

public record Negociacion(long idOferta, String estado, BigDecimal monto, long idTipoPago, long idComprador,
        String comprador, long idInmueble, String direccion, long idVendedor, long idAgente) {

    public long contraparte(String parte) {
        return VENDEDOR.equals(parte) ? idComprador : idVendedor;
    }
}
