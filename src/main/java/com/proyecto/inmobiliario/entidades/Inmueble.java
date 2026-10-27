package com.proyecto.inmobiliario.entidades;

import java.math.BigDecimal;
import java.util.List;

public record Inmueble(Long id, String direccion, BigDecimal precio, BigDecimal metraje, Integer disponibilidad,
        Long idTipoInmueble, String tipoInmueble, Long idVendedor, String vendedor, Long idAgente,
        String agente, List<Detalle> detalles) {

    public Inmueble conDetalles(List<Detalle> d) {
        return new Inmueble(id, direccion, precio, metraje, disponibilidad, idTipoInmueble, tipoInmueble,
                idVendedor, vendedor, idAgente, agente, d);
    }
}
