package com.proyecto.inmobiliario.entidades;

import java.time.LocalDateTime;

public record Notificacion(Long id, String mensaje, LocalDateTime fecha, Integer leida, Long idUsuario,
        Long idAgente) {
}
