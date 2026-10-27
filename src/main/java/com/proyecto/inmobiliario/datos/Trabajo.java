package com.proyecto.inmobiliario.datos;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface Trabajo<T> {
    T en(Connection c) throws SQLException;
}
