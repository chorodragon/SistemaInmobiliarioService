package com.proyecto.inmobiliario.datos;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface Fila<T> {
    T de(ResultSet rs) throws SQLException;
}
