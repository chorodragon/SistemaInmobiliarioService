package com.proyecto.inmobiliario.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Oferta(Long id, BigDecimal monto, String tiempo, String estado, LocalDateTime fecha,
        Long idComprador, String comprador, Long idInmueble, String inmueble, Long idTipoPago,
        String tipoPago) {
}
