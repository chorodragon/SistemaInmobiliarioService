package com.proyecto.inmobiliario.datos;

import static com.proyecto.inmobiliario.datos.BaseDatos.lista;
import static com.proyecto.inmobiliario.datos.BaseDatos.uno;
import static com.proyecto.inmobiliario.util.Funciones.largo;

import com.proyecto.inmobiliario.entidades.Catalogo;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class CatalogoDatos {

    private static final Fila<Catalogo> CATALOGO = rs -> new Catalogo(largo(rs, "id"), rs.getString("nombre"),
            rs.getString("descripcion"));

    private CatalogoDatos() {
    }

    public static List<Catalogo> tiposInmueble(Connection c) throws SQLException {
        return lista(c, CATALOGO, "SELECT idTipoInmueble id, nombre, NULL descripcion FROM TipoInmueble ORDER BY id");
    }

    public static List<Catalogo> tiposPago(Connection c) throws SQLException {
        return lista(c, CATALOGO, "SELECT idTipoPago id, nombre, descripcion FROM TipoPago ORDER BY id");
    }

    public static List<Catalogo> caracteristicas(Connection c) throws SQLException {
        return lista(c, CATALOGO,
                "SELECT idCaracteristicas id, caracteristica nombre, NULL descripcion FROM Caracteristicas ORDER BY id");
    }

    public static String nombreTipoPago(Connection c, Long idTipoPago) throws SQLException {
        return uno(c, rs -> rs.getString(1), "SELECT nombre FROM TipoPago WHERE idTipoPago = ?", idTipoPago);
    }
}
