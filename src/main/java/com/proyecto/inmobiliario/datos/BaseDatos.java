package com.proyecto.inmobiliario.datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseDatos {

    @Value("${db.url}")
    private String url;
    @Value("${db.user}")
    private String usuario;
    @Value("${db.pass}")
    private String clave;

    public <T> T conexion(Trabajo<T> trabajo) {
        try (Connection c = DriverManager.getConnection(url, usuario, clave)) {
            c.setAutoCommit(false);
            try {
                T resultado = trabajo.en(c);
                c.commit();
                return resultado;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static PreparedStatement preparar(Connection c, String[] claves, String sql, Object... params)
            throws SQLException {
        PreparedStatement ps = claves == null ? c.prepareStatement(sql) : c.prepareStatement(sql, claves);
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                ps.setNull(i + 1, Types.VARCHAR);
            } else {
                ps.setObject(i + 1, params[i]);
            }
        }
        return ps;
    }

    public static <T> List<T> lista(Connection c, Fila<T> fila, String sql, Object... params) throws SQLException {
        System.out.println("sql: " + sql);
        try (PreparedStatement ps = preparar(c, null, sql, params); ResultSet rs = ps.executeQuery()) {
            List<T> resultado = new ArrayList<>();
            while (rs.next()) {
                resultado.add(fila.de(rs));
            }
            return resultado;
        }
    }

    public static <T> T uno(Connection c, Fila<T> fila, String sql, Object... params) throws SQLException {
        List<T> resultado = lista(c, fila, sql, params);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public static int ejecutar(Connection c, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = preparar(c, null, sql, params)) {
            return ps.executeUpdate();
        }
    }

    public static long insertar(Connection c, String columnaId, String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = preparar(c, new String[] { columnaId }, sql, params)) {
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }
}
