package com.proyecto.inmobiliario.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Venta(Long idContrato, LocalDateTime fecha, BigDecimal monto, Long idInmueble, String inmueble,
        Long idComprador, String comprador) {
}
