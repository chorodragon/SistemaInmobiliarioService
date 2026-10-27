package com.proyecto.inmobiliario.datos;

import static com.proyecto.inmobiliario.datos.BaseDatos.ejecutar;
import static com.proyecto.inmobiliario.datos.BaseDatos.lista;
import static com.proyecto.inmobiliario.datos.BaseDatos.uno;
import static com.proyecto.inmobiliario.util.Funciones.entero;
import static com.proyecto.inmobiliario.util.Funciones.fecha;
import static com.proyecto.inmobiliario.util.Funciones.largo;

import com.proyecto.inmobiliario.dto.ContraOfertaReq;
import com.proyecto.inmobiliario.entidades.ContraOferta;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class ContraOfertaDatos {

    private static final String SQL_CONTRAOFERTA = """
            SELECT co.idContraOferta, co.monto, co.tiempo, co.prioridad, co.estado, co.origen, co.fecha,
                   co.Oferta_idOferta, tp.idTipoPago, tp.nombre tipoPago
            FROM ContraOferta co
            JOIN TipoPago tp ON tp.idTipoPago = co.TipoPago_idTipoPago
            """;

    private static final Fila<ContraOferta> CONTRAOFERTA = rs -> new ContraOferta(largo(rs, "idContraOferta"),
            rs.getBigDecimal("monto"), rs.getString("tiempo"), entero(rs, "prioridad"), rs.getString("estado"),
            rs.getString("origen"), fecha(rs, "fecha"), largo(rs, "Oferta_idOferta"), largo(rs, "idTipoPago"),
            rs.getString("tipoPago"));

    private ContraOfertaDatos() {
    }

    public static List<ContraOferta> porOferta(Connection c, long idOferta) throws SQLException {
        return lista(c, CONTRAOFERTA,
                SQL_CONTRAOFERTA + " WHERE co.Oferta_idOferta = ? ORDER BY co.fecha, co.idContraOferta", idOferta);
    }

    public static ContraOferta porOfertaYEstado(Connection c, long idOferta, String estado) throws SQLException {
        return uno(c, CONTRAOFERTA,
                SQL_CONTRAOFERTA + " WHERE co.Oferta_idOferta = ? AND co.estado = ?", idOferta, estado);
    }

    public static long insertar(Connection c, long idOferta, ContraOfertaReq r, String tiempo, int prioridad,
            String estado) throws SQLException {
        return BaseDatos.insertar(c, "idContraOferta", """
                INSERT INTO ContraOferta (monto, tiempo, prioridad, estado, origen, fecha, Oferta_idOferta,
                                          TipoPago_idTipoPago)
                VALUES (?, ?, ?, ?, ?, SYSDATE, ?, ?)""",
                r.monto(), tiempo, prioridad, estado, r.origen(), idOferta, r.idTipoPago());
    }

    public static void cambiarEstado(Connection c, long idContraOferta, String estado) throws SQLException {
        ejecutar(c, "UPDATE ContraOferta SET estado = ? WHERE idContraOferta = ?", estado, idContraOferta);
    }

    public static void cambiarEstadoPorOferta(Connection c, long idOferta, String nuevo, String actual)
            throws SQLException {
        ejecutar(c, "UPDATE ContraOferta SET estado = ? WHERE Oferta_idOferta = ? AND estado = ?",
                nuevo, idOferta, actual);
    }

    public static void cambiarEstadoOtrasOfertasDelInmueble(Connection c, long idInmueble, long idOferta,
            String nuevo, String actual) throws SQLException {
        ejecutar(c,
                """
                        UPDATE ContraOferta SET estado = ?
                        WHERE estado = ?
                          AND Oferta_idOferta IN (SELECT idOferta FROM Oferta WHERE Inmueble_idInmueble = ? AND idOferta <> ?)""",
                nuevo, actual, idInmueble, idOferta);
    }
}
