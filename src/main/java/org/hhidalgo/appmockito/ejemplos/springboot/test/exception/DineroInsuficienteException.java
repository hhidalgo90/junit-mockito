package org.hhidalgo.appmockito.ejemplos.springboot.test.exception;

public class DineroInsuficienteException extends RuntimeException {
    public DineroInsuficienteException(String message) {
        super(message);
    }
}
