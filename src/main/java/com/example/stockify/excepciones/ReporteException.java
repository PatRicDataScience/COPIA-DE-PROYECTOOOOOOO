package com.example.stockify.excepciones;

public class ReporteException extends RuntimeException {
    public ReporteException(String mensaje) {
        super(mensaje);
    }

    public ReporteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}