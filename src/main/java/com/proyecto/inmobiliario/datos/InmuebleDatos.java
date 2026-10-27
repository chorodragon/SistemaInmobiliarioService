package com.proyecto.inmobiliario.datos;

import static com.proyecto.inmobiliario.datos.BaseDatos.ejecutar;
import static com.proyecto.inmobiliario.datos.BaseDatos.lista;
import static com.proyecto.inmobiliario.datos.BaseDatos.uno;
import static com.proyecto.inmobiliario.util.Funciones.entero;
import static com.proyecto.inmobiliario.util.Funciones.largo;

import com.proyecto.inmobiliario.dto.DetalleReq;
import com.proyecto.inmobiliario.dto.InmuebleReq;
import com.proyecto.inmobiliario.entidades.Detalle;
import com.proyecto.inmobiliario.entidades.Inmueble;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class InmuebleDatos {

    private static final String SQL_INMUEBLE = """
            SELECT i.idInmueble, i.direccion, i.precio, i.metraje, i.disponibilidad,
                   t.idTipoInmueble, t.nombre tipoInmueble,
                   v.idUsuario idVendedor, v.nombre || ' ' || v.apellido vendedor,
                   a.idAgente, a.nombre || ' ' || a.apellido agente
            FROM Inmueble i
            JOIN TipoInmueble t ON t.idTipoInmueble = i.TipoInmueble_idTipoInmueble
            JOIN Usuario v ON v.idUsuario = i.Vendedor_idUsuario
            JOIN Agente a ON a.idAgente = i.Agente_idAgente
            """;

    private static final Fila<Inmueble> INMUEBLE = rs -> new Inmueble(largo(rs, "idInmueble"),
            rs.getString("direccion"), rs.getBigDecimal("precio"), rs.getBigDecimal("metraje"),
            entero(rs, "disponibilidad"), largo(rs, "idTipoInmueble"), rs.getString("tipoInmueble"),
            largo(rs, "idVendedor"), rs.getString("vendedor"), largo(rs, "idAgente"), rs.getString("agente"), null);

    private InmuebleDatos() {
    }

    public static List<Inmueble> porAgente(Connection c, long idAgente) throws SQLException {
        return lista(c, INMUEBLE, SQL_INMUEBLE + " WHERE i.Agente_idAgente = ? ORDER BY i.idInmueble", idAgente);
    }

    public static List<Inmueble> porVendedor(Connection c, long idVendedor) throws SQLException {
        return lista(c, INMUEBLE, SQL_INMUEBLE + " WHERE i.Vendedor_idUsuario = ? ORDER BY i.idInmueble", idVendedor);
    }

    public static List<Inmueble> buscar(Connection c, String direccion, BigDecimal metrajeMin,
            BigDecimal metrajeMax, BigDecimal precioMin, BigDecimal precioMax) throws SQLException {
        return lista(c, INMUEBLE, SQL_INMUEBLE + """
                WHERE i.disponibilidad = 1
                  AND (? IS NULL OR UPPER(i.direccion) LIKE '%' || UPPER(?) || '%')
                  AND (? IS NULL OR i.metraje >= ?)
                  AND (? IS NULL OR i.metraje <= ?)
                  AND (? IS NULL OR i.precio >= ?)
                  AND (? IS NULL OR i.precio <= ?)
                ORDER BY i.idInmueble""",
                direccion, direccion, metrajeMin, metrajeMin, metrajeMax, metrajeMax,
                precioMin, precioMin, precioMax, precioMax);
    }

    public static Inmueble porId(Connection c, long id) throws SQLException {
        return uno(c, INMUEBLE, SQL_INMUEBLE + " WHERE i.idInmueble = ?", id);
    }

    public static List<Detalle> detalles(Connection c, long idInmueble) throws SQLException {
        return lista(c,
                rs -> new Detalle(largo(rs, "idCaracteristicas"), rs.getString("caracteristica"),
                        rs.getString("descripcion")),
                """
                        SELECT ca.idCaracteristicas, ca.caracteristica, d.descripcion
                        FROM Detalle d JOIN Caracteristicas ca ON ca.idCaracteristicas = d.Caracteristicas_idCaracteristicas
                        WHERE d.Inmueble_idInmueble = ?
                        ORDER BY ca.idCaracteristicas""",
                idInmueble);
    }

    public static long insertar(Connection c, InmuebleReq r) throws SQLException {
        long id = BaseDatos.insertar(c, "idInmueble", """
                INSERT INTO Inmueble (direccion, precio, metraje, disponibilidad, Vendedor_idUsuario,
                                      Agente_idAgente, TipoInmueble_idTipoInmueble)
                VALUES (?, ?, ?, 1, ?, ?, ?)""",
                r.direccion(), r.precio(), r.metraje(), r.idVendedor(), r.idAgente(), r.idTipoInmueble());
        if (r.detalles() != null) {
            for (DetalleReq d : r.detalles()) {
                ejecutar(c, """
                        INSERT INTO Detalle (Caracteristicas_idCaracteristicas, Inmueble_idInmueble, descripcion)
                        VALUES (?, ?, ?)""",
                        d.idCaracteristica(), id, d.descripcion());
            }
        }
        return id;
    }

    public static Integer disponibilidad(Connection c, Long idInmueble) throws SQLException {
        return uno(c, rs -> entero(rs, "disponibilidad"),
                "SELECT disponibilidad FROM Inmueble WHERE idInmueble = ?", idInmueble);
    }

    public static void marcarNoDisponible(Connection c, long idInmueble) throws SQLException {
        ejecutar(c, "UPDATE Inmueble SET disponibilidad = 0 WHERE idInmueble = ?", idInmueble);
    }

    public static void marcarDisponible(Connection c, long idInmueble) throws SQLException {
        ejecutar(c, "UPDATE Inmueble SET disponibilidad = 1 WHERE idInmueble = ?", idInmueble);
    }
}
