package com.proyecto.inmobiliario.util;

import static com.proyecto.inmobiliario.util.Constantes.COMPRADOR;
import static com.proyecto.inmobiliario.util.Constantes.ESTADOS;
import static com.proyecto.inmobiliario.util.Constantes.VENDEDOR;

import com.proyecto.inmobiliario.excepciones.Rechazo;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HexFormat;

public final class Funciones {

    private Funciones() {
    }

    public static String md5(String texto) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5")
                    .digest(String.valueOf(texto).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Long largo(ResultSet rs, String columna) throws SQLException {
        BigDecimal v = rs.getBigDecimal(columna);
        return v == null ? null : v.longValue();
    }

    public static Integer entero(ResultSet rs, String columna) throws SQLException {
        BigDecimal v = rs.getBigDecimal(columna);
        return v == null ? null : v.intValue();
    }

    public static LocalDateTime fecha(ResultSet rs, String columna) throws SQLException {
        Timestamp t = rs.getTimestamp(columna);
        return t == null ? null : t.toLocalDateTime();
    }

    public static String estado(ResultSet rs, String columna) throws SQLException {
        Integer v = entero(rs, columna);
        return v == null ? null : ESTADOS.get(v);
    }

    public static String tiempoSegunPago(String tipoPago, String tiempo) {
        if (!"Credito".equals(tipoPago)) {
            return null;
        }
        if (tiempo == null || tiempo.isBlank()) {
            throw new Rechazo("El tiempo en días es obligatorio cuando el pago es a crédito");
        }
        return tiempo.strip();
    }

    public static void validarParte(String parte) {
        if (!VENDEDOR.equals(parte) && !COMPRADOR.equals(parte)) {
            throw new Rechazo("Parte inválida: use vendedor o comprador");
        }
    }
}
