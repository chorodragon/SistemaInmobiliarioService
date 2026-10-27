package com.proyecto.inmobiliario.dto;

import java.math.BigDecimal;

public record ContraOfertaReq(String origen, Long idTipoPago, BigDecimal monto, String tiempo) {
}
