package org.hhidalgo.appmockito.ejemplos.springboot.test;

import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Banco;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Cuenta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

public class Datos {
    public static final Optional<Cuenta> CUENTA_001 = Optional.of(new Cuenta(1L, "Andres", new BigDecimal("1000")));
    public static final Optional<Cuenta> CUENTA_002 = Optional.of(new Cuenta(2L, "Hector", new BigDecimal("2000")));
    public static final Optional<Banco> BANCO = Optional.of(new Banco(1L, "Banco de Chile", 0));
}
