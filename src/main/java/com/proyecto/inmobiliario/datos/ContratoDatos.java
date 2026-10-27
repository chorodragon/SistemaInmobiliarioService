package com.proyecto.inmobiliario.datos;

import static com.proyecto.inmobiliario.datos.BaseDatos.ejecutar;
import static com.proyecto.inmobiliario.datos.BaseDatos.lista;
import static com.proyecto.inmobiliario.datos.BaseDatos.uno;
import static com.proyecto.inmobiliario.util.Funciones.estado;
import static com.proyecto.inmobiliario.util.Funciones.fecha;
import static com.proyecto.inmobiliario.util.Funciones.largo;

import com.proyecto.inmobiliario.entidades.Contrato;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class ContratoDatos {

    private static final String SQL_CONTRATO = """
            SELECT ct.idContrato, ct.fecha, ct.monto, ct.estado, o.idOferta,
                   i.idInmueble, i.direccion inmueble,
                   u.idUsuario idComprador, u.nombre || ' ' || u.apellido comprador,
                   v.idUsuario idVendedor, v.nombre || ' ' || v.apellido vendedor,
                   a.idAgente, a.nombre || ' ' || a.apellido agente,
                   tp.idTipoPago, tp.nombre tipoPago
            FROM Contrato ct
            JOIN Oferta o ON o.idOferta = ct.Oferta_idOferta
            JOIN Inmueble i ON i.idInmueble = o.Inmueble_idInmueble
            JOIN Usuario u ON u.idUsuario = o.Comprador_idUsuario
            JOIN Usuario v ON v.idUsuario = i.Vendedor_idUsuario
            JOIN Agente a ON a.idAgente = i.Agente_idAgente
            JOIN TipoPago tp ON tp.idTipoPago = ct.TipoPago_idTipoPago
            """;

    private static final Fila<Contrato> CONTRATO = rs -> new Contrato(largo(rs, "idContrato"), fecha(rs, "fecha"),
            rs.getBigDecimal("monto"), estado(rs, "estado"), largo(rs, "idOferta"), largo(rs, "idInmueble"),
            rs.getString("inmueble"), largo(rs, "idComprador"), rs.getString("comprador"), largo(rs, "idVendedor"),
            rs.getString("vendedor"), largo(rs, "idAgente"), rs.getString("agente"), largo(rs, "idTipoPago"),
            rs.getString("tipoPago"));

    private ContratoDatos() {
    }

    public static List<Contrato> buscar(Connection c, Long idComprador, Long idVendedor, Long idAgente)
            throws SQLException {
        return lista(c, CONTRATO, SQL_CONTRATO + """
                WHERE (? IS NULL OR o.Comprador_idUsuario = ?)
                  AND (? IS NULL OR i.Vendedor_idUsuario = ?)
                  AND (? IS NULL OR i.Agente_idAgente = ?)
                ORDER BY ct.fecha DESC""",
                idComprador, idComprador, idVendedor, idVendedor, idAgente, idAgente);
    }

    public static Contrato porId(Connection c, long id) throws SQLException {
        return uno(c, CONTRATO, SQL_CONTRATO + " WHERE ct.idContrato = ?", id);
    }

    public static long insertar(Connection c, BigDecimal monto, int estado, long idTipoPago, long idOferta)
            throws SQLException {
        return BaseDatos.insertar(c, "idContrato", """
                INSERT INTO Contrato (fecha, monto, estado, TipoPago_idTipoPago, Oferta_idOferta)
                VALUES (SYSDATE, ?, ?, ?, ?)""",
                monto, estado, idTipoPago, idOferta);
    }

    public static void cambiarEstado(Connection c, long idContrato, int estado) throws SQLException {
        ejecutar(c, "UPDATE Contrato SET estado = ? WHERE idContrato = ?", estado, idContrato);
    }
}
