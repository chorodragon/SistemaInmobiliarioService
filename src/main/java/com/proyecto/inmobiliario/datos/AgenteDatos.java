package com.proyecto.inmobiliario.datos;

import static com.proyecto.inmobiliario.datos.BaseDatos.lista;
import static com.proyecto.inmobiliario.datos.BaseDatos.uno;
import static com.proyecto.inmobiliario.util.Funciones.fecha;
import static com.proyecto.inmobiliario.util.Funciones.largo;

import com.proyecto.inmobiliario.dto.Sesion;
import com.proyecto.inmobiliario.entidades.Agente;
import com.proyecto.inmobiliario.entidades.Venta;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class AgenteDatos {

    private AgenteDatos() {
    }

    public static Sesion login(Connection c, String usuario, String hash, String tipo) throws SQLException {
        return uno(c, rs -> new Sesion(largo(rs, "id"), rs.getString("nombre"), rs.getString("apellido"), tipo),
                "SELECT idAgente id, nombre, apellido FROM Agente WHERE usuario = ? AND pass = ?",
                usuario, hash);
    }

    public static List<Agente> buscar(Connection c, String texto) throws SQLException {
        return lista(c,
                rs -> new Agente(largo(rs, "idAgente"), rs.getString("codigo"), rs.getString("nombre"),
                        rs.getString("apellido"), rs.getString("email")),
                """
                        SELECT idAgente, codigo, nombre, apellido, email FROM Agente
                        WHERE UPPER(nombre || ' ' || apellido) LIKE ? OR codigo = ?
                        ORDER BY nombre, apellido""",
                "%" + texto + "%", texto);
    }

    public static List<Venta> ventas(Connection c, long idAgente) throws SQLException {
        return lista(c,
                rs -> new Venta(largo(rs, "idContrato"), fecha(rs, "fecha"), rs.getBigDecimal("monto"),
                        largo(rs, "idInmueble"), rs.getString("direccion"), largo(rs, "idUsuario"),
                        rs.getString("comprador")),
                """
                        SELECT ct.idContrato, ct.fecha, ct.monto, i.idInmueble, i.direccion,
                               u.idUsuario, u.nombre || ' ' || u.apellido comprador
                        FROM Contrato ct
                        JOIN Oferta o ON o.idOferta = ct.Oferta_idOferta
                        JOIN Inmueble i ON i.idInmueble = o.Inmueble_idInmueble
                        JOIN Usuario u ON u.idUsuario = o.Comprador_idUsuario
                        WHERE i.Agente_idAgente = ? AND ct.estado = 1
                        ORDER BY ct.fecha DESC""",
                idAgente);
    }
}
