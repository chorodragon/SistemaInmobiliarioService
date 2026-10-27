package com.proyecto.inmobiliario.datos;

import static com.proyecto.inmobiliario.datos.BaseDatos.ejecutar;
import static com.proyecto.inmobiliario.datos.BaseDatos.lista;
import static com.proyecto.inmobiliario.datos.BaseDatos.uno;
import static com.proyecto.inmobiliario.util.Funciones.fecha;
import static com.proyecto.inmobiliario.util.Funciones.largo;

import com.proyecto.inmobiliario.dto.OfertaReq;
import com.proyecto.inmobiliario.entidades.Negociacion;
import com.proyecto.inmobiliario.entidades.Oferta;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class OfertaDatos {

    private static final String SQL_OFERTA = """
            SELECT o.idOferta, o.monto, o.tiempo, o.estado, o.fecha,
                   u.idUsuario idComprador, u.nombre || ' ' || u.apellido comprador,
                   i.idInmueble, i.direccion inmueble, tp.idTipoPago, tp.nombre tipoPago
            FROM Oferta o
            JOIN Usuario u ON u.idUsuario = o.Comprador_idUsuario
            JOIN Inmueble i ON i.idInmueble = o.Inmueble_idInmueble
            JOIN TipoPago tp ON tp.idTipoPago = o.TipoPago_idTipoPago
            """;

    private static final Fila<Oferta> OFERTA = rs -> new Oferta(largo(rs, "idOferta"), rs.getBigDecimal("monto"),
            rs.getString("tiempo"), rs.getString("estado"), fecha(rs, "fecha"), largo(rs, "idComprador"),
            rs.getString("comprador"), largo(rs, "idInmueble"), rs.getString("inmueble"), largo(rs, "idTipoPago"),
            rs.getString("tipoPago"));

    private OfertaDatos() {
    }

    public static List<Oferta> porInmueble(Connection c, long idInmueble) throws SQLException {
        return lista(c, OFERTA, SQL_OFERTA + """
                WHERE o.Inmueble_idInmueble = ?
                ORDER BY CASE WHEN tp.nombre = 'Efectivo' THEN 0 ELSE 1 END,
                         TO_NUMBER(o.tiempo DEFAULT NULL ON CONVERSION ERROR) NULLS FIRST, o.fecha""",
                idInmueble);
    }

    public static List<Oferta> porComprador(Connection c, long idComprador) throws SQLException {
        return lista(c, OFERTA, SQL_OFERTA + " WHERE o.Comprador_idUsuario = ? ORDER BY o.fecha DESC", idComprador);
    }

    public static Oferta porId(Connection c, long id) throws SQLException {
        return uno(c, OFERTA, SQL_OFERTA + " WHERE o.idOferta = ?", id);
    }

    public static long insertar(Connection c, OfertaReq r, String tiempo, String estado) throws SQLException {
        return BaseDatos.insertar(c, "idOferta", """
                INSERT INTO Oferta (monto, tiempo, estado, fecha, Comprador_idUsuario, Inmueble_idInmueble,
                                    TipoPago_idTipoPago)
                VALUES (?, ?, ?, SYSDATE, ?, ?, ?)""",
                r.monto(), tiempo, estado, r.idComprador(), r.idInmueble(), r.idTipoPago());
    }

    public static Negociacion negociacion(Connection c, long idOferta) throws SQLException {
        return uno(c,
                rs -> new Negociacion(rs.getLong("idOferta"), rs.getString("estado"), rs.getBigDecimal("monto"),
                        rs.getLong("TipoPago_idTipoPago"), rs.getLong("Comprador_idUsuario"),
                        rs.getString("comprador"), rs.getLong("idInmueble"), rs.getString("direccion"),
                        rs.getLong("Vendedor_idUsuario"), rs.getLong("Agente_idAgente")),
                """
                        SELECT o.idOferta, o.estado, o.monto, o.TipoPago_idTipoPago, o.Comprador_idUsuario,
                               u.nombre || ' ' || u.apellido comprador,
                               i.idInmueble, i.direccion, i.Vendedor_idUsuario, i.Agente_idAgente
                        FROM Oferta o
                        JOIN Usuario u ON u.idUsuario = o.Comprador_idUsuario
                        JOIN Inmueble i ON i.idInmueble = o.Inmueble_idInmueble
                        WHERE o.idOferta = ?""",
                idOferta);
    }

    public static void cambiarEstado(Connection c, long idOferta, String estado) throws SQLException {
        ejecutar(c, "UPDATE Oferta SET estado = ? WHERE idOferta = ?", estado, idOferta);
    }

    public static List<long[]> otrasDelInmueble(Connection c, long idInmueble, long idOferta, String estado)
            throws SQLException {
        return lista(c, rs -> new long[] { rs.getLong(1), rs.getLong(2) },
                "SELECT idOferta, Comprador_idUsuario FROM Oferta WHERE Inmueble_idInmueble = ? AND idOferta <> ? AND estado = ?",
                idInmueble, idOferta, estado);
    }

    public static void cambiarEstadoOtrasDelInmueble(Connection c, long idInmueble, long idOferta, String nuevo,
            String actual) throws SQLException {
        ejecutar(c, "UPDATE Oferta SET estado = ? WHERE Inmueble_idInmueble = ? AND idOferta <> ? AND estado = ?",
                nuevo, idInmueble, idOferta, actual);
    }
}
