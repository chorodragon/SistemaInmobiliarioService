package com.proyecto.inmobiliario.dto;

import java.math.BigDecimal;
import java.util.List;

public record InmuebleReq(String direccion, BigDecimal precio, BigDecimal metraje, Long idVendedor,
        Long idAgente, Long idTipoInmueble, List<DetalleReq> detalles) {
}
