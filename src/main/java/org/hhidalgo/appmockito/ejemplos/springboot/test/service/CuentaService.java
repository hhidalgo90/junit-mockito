package org.hhidalgo.appmockito.ejemplos.springboot.test.service;

import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface CuentaService {

    Cuenta findById(Long id);

    int revisarTotalTransferencia(Long bancoId);

    BigDecimal revisarSaldo(Long cuentaId);

    void transferir(Long nroCtaOrigen, Long nroCtaDestino, BigDecimal monto, Long bancoId);

    List<Cuenta> findAll();

    Cuenta save(Cuenta cuenta);

    void deleteById(Long id);

}

