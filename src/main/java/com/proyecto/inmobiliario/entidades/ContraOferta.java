package com.proyecto.inmobiliario.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContraOferta(Long id, BigDecimal monto, String tiempo, Integer prioridad, String estado,
        String origen, LocalDateTime fecha, Long idOferta, Long idTipoPago, String tipoPago) {
}
