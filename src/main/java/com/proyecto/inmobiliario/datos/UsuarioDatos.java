package com.proyecto.inmobiliario.datos;

import static com.proyecto.inmobiliario.datos.BaseDatos.ejecutar;
import static com.proyecto.inmobiliario.datos.BaseDatos.insertar;
import static com.proyecto.inmobiliario.datos.BaseDatos.uno;
import static com.proyecto.inmobiliario.util.Constantes.COMPRADOR;
import static com.proyecto.inmobiliario.util.Funciones.largo;

import com.proyecto.inmobiliario.dto.ClienteReq;
import com.proyecto.inmobiliario.dto.Sesion;
import java.sql.Connection;
import java.sql.SQLException;

public final class UsuarioDatos {

    private UsuarioDatos() {
    }

    public static Sesion login(Connection c, String usuario, String hash, String tipo) throws SQLException {
        return uno(c, rs -> new Sesion(largo(rs, "id"), rs.getString("nombre"), rs.getString("apellido"), tipo),
                "SELECT u.idUsuario id, u.nombre, u.apellido FROM Usuario u JOIN "
                        + (COMPRADOR.equals(tipo) ? "Comprador" : "Vendedor")
                        + " x ON x.idUsuario = u.idUsuario WHERE u.usuario = ? AND u.pass = ?",
                usuario, hash);
    }

    public static Long idPorUsuario(Connection c, String usuario) throws SQLException {
        return uno(c, rs -> rs.getLong(1), "SELECT idUsuario FROM Usuario WHERE usuario = ?", usuario);
    }

    public static long insertarCliente(Connection c, ClienteReq r, String hash) throws SQLException {
        long id = insertar(c, "idUsuario", """
                INSERT INTO Usuario (nombre, apellido, telefono, direccion, edad, nacionalidad, estadoCivil,
                                     email, usuario, pass)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                r.nombre(), r.apellido(), r.telefono(), r.direccion(), r.edad(), r.nacionalidad(),
                r.estadoCivil(), r.email(), r.usuario(), hash);
        ejecutar(c, "INSERT INTO Comprador (idUsuario) VALUES (?)", id);
        return id;
    }

    public static void sumarVenta(Connection c, long idVendedor) throws SQLException {
        ejecutar(c, "UPDATE Vendedor SET cant_ventas = NVL(cant_ventas, 0) + 1 WHERE idUsuario = ?", idVendedor);
    }
}
