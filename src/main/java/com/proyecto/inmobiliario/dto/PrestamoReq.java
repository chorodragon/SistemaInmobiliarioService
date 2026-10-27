package com.proyecto.inmobiliario.dto;

import java.math.BigDecimal;

public record PrestamoReq(String banco, BigDecimal monto, String plazo, BigDecimal intereses) {
}
