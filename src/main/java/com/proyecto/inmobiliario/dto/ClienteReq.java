package com.proyecto.inmobiliario.dto;

public record ClienteReq(String nombre, String apellido, String telefono, String direccion, Integer edad,
        String nacionalidad, String estadoCivil, String email, String usuario, String pass) {
}
