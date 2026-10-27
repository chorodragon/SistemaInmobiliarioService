package com.proyecto.inmobiliario.datos;

import static com.proyecto.inmobiliario.datos.BaseDatos.ejecutar;
import static com.proyecto.inmobiliario.datos.BaseDatos.lista;
import static com.proyecto.inmobiliario.util.Funciones.entero;
import static com.proyecto.inmobiliario.util.Funciones.fecha;
import static com.proyecto.inmobiliario.util.Funciones.largo;

import com.proyecto.inmobiliario.entidades.Notificacion;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class NotificacionDatos {

    private NotificacionDatos() {
    }

    public static List<Notificacion> buscar(Connection c, Long idUsuario, Long idAgente) throws SQLException {
        return lista(c,
                rs -> new Notificacion(largo(rs, "idNotificacion"), rs.getString("mensaje"), fecha(rs, "fecha"),
                        entero(rs, "leida"), largo(rs, "Usuario_idUsuario"), largo(rs, "Agente_idAgente")),
                """
                        SELECT idNotificacion, mensaje, fecha, leida, Usuario_idUsuario, Agente_idAgente
                        FROM Notificaciones
                        WHERE (? IS NULL OR Usuario_idUsuario = ?)
                          AND (? IS NULL OR Agente_idAgente = ?)
                        ORDER BY fecha DESC, idNotificacion DESC""",
                idUsuario, idUsuario, idAgente, idAgente);
    }

    public static void insertar(Connection c, long idUsuario, long idAgente, String mensaje) throws SQLException {
        ejecutar(c, """
                INSERT INTO Notificaciones (mensaje, fecha, leida, Usuario_idUsuario, Agente_idAgente)
                VALUES (?, SYSDATE, 0, ?, ?)""",
                mensaje, idUsuario, idAgente);
    }

    public static void marcarLeida(Connection c, long id) throws SQLException {
        ejecutar(c, "UPDATE Notificaciones SET leida = 1 WHERE idNotificacion = ?", id);
    }
}
