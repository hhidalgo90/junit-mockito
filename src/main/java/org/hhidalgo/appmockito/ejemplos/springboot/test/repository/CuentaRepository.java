package org.hhidalgo.appmockito.ejemplos.springboot.test.repository;

import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

    @Query("select c from Cuenta c where c.nombrePersona =?1")
    Optional<Cuenta> findByNombrePersona(String nombrePersona);
}
