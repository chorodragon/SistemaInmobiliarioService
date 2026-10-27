package com.proyecto.inmobiliario.entidades;

import java.math.BigDecimal;

public record Prestamo(Long id, String banco, BigDecimal monto, String plazo, BigDecimal intereses,
        String estado, Long idContrato) {
}
