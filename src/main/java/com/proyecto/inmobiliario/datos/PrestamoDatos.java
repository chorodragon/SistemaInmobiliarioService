package com.proyecto.inmobiliario.datos;

import static com.proyecto.inmobiliario.datos.BaseDatos.ejecutar;
import static com.proyecto.inmobiliario.datos.BaseDatos.lista;
import static com.proyecto.inmobiliario.datos.BaseDatos.uno;
import static com.proyecto.inmobiliario.util.Funciones.estado;
import static com.proyecto.inmobiliario.util.Funciones.largo;

import com.proyecto.inmobiliario.dto.PrestamoReq;
import com.proyecto.inmobiliario.entidades.Prestamo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class PrestamoDatos {

    private static final String SQL_PRESTAMO = """
            SELECT idPrestamos, banco, monto, plazo, intereses, estado, Contrato_idContrato
            FROM Prestamos
            """;

    private static final Fila<Prestamo> PRESTAMO = rs -> new Prestamo(largo(rs, "idPrestamos"), rs.getString("banco"),
            rs.getBigDecimal("monto"), rs.getString("plazo"), rs.getBigDecimal("intereses"), estado(rs, "estado"),
            largo(rs, "Contrato_idContrato"));

    private PrestamoDatos() {
    }

    public static List<Prestamo> porContrato(Connection c, long idContrato) throws SQLException {
        return lista(c, PRESTAMO, SQL_PRESTAMO + " WHERE Contrato_idContrato = ? ORDER BY idPrestamos", idContrato);
    }

    public static Prestamo porId(Connection c, long id) throws SQLException {
        return uno(c, PRESTAMO, SQL_PRESTAMO + " WHERE idPrestamos = ?", id);
    }

    public static Long idPorContratoYEstado(Connection c, long idContrato, int estado) throws SQLException {
        return uno(c, rs -> rs.getLong(1),
                "SELECT idPrestamos FROM Prestamos WHERE Contrato_idContrato = ? AND estado = ?",
                idContrato, estado);
    }

    public static long insertar(Connection c, long idContrato, PrestamoReq r, int estado) throws SQLException {
        return BaseDatos.insertar(c, "idPrestamos", """
                INSERT INTO Prestamos (banco, monto, plazo, intereses, estado, Contrato_idContrato)
                VALUES (?, ?, ?, ?, ?, ?)""",
                r.banco(), r.monto(), r.plazo(), r.intereses(), estado, idContrato);
    }

    public static void cambiarEstado(Connection c, long idPrestamo, int estado) throws SQLException {
        ejecutar(c, "UPDATE Prestamos SET estado = ? WHERE idPrestamos = ?", estado, idPrestamo);
    }

    public static void cambiarEstadoPorContrato(Connection c, long idContrato, int estado) throws SQLException {
        ejecutar(c, "UPDATE Prestamos SET estado = ? WHERE Contrato_idContrato = ?", estado, idContrato);
    }
}
