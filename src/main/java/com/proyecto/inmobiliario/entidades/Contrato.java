package com.proyecto.inmobiliario.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Contrato(Long id, LocalDateTime fecha, BigDecimal monto, String estado, Long idOferta,
        Long idInmueble, String inmueble, Long idComprador, String comprador, Long idVendedor,
        String vendedor, Long idAgente, String agente, Long idTipoPago, String tipoPago) {
}
