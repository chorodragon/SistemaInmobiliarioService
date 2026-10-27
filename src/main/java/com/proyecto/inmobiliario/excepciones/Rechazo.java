package com.proyecto.inmobiliario.excepciones;

public class Rechazo extends RuntimeException {

    public Rechazo(String mensaje) {
        super(mensaje);
    }
}
