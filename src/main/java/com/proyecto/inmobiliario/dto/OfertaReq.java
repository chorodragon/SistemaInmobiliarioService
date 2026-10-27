package com.proyecto.inmobiliario.dto;

import java.math.BigDecimal;

public record OfertaReq(Long idComprador, Long idInmueble, Long idTipoPago, BigDecimal monto, String tiempo) {
}
