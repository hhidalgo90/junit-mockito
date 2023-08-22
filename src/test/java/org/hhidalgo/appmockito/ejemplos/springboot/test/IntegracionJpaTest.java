package org.hhidalgo.appmockito.ejemplos.springboot.test;

import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Cuenta;
import org.hhidalgo.appmockito.ejemplos.springboot.test.repository.CuentaRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integracion_jpa")
@DataJpaTest
public class IntegracionJpaTest {

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById() {
        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);

        assertTrue(cuenta.isPresent());
        assertEquals("Andres", cuenta.orElseThrow().getNombrePersona());
    }

    @Test
    void testFindByPersona() {
        Optional<Cuenta> cuenta = cuentaRepository.findByNombrePersona("Andres");

        assertTrue(cuenta.isPresent());
        assertEquals("Andres", cuenta.orElseThrow().getNombrePersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());
    }

    @Test
    void testFindByPersonaThrowException() {
        Optional<Cuenta> cuenta = cuentaRepository.findByNombrePersona("Boby"); //probamosd que no existe

        assertThrows(NoSuchElementException.class, cuenta::orElseThrow);
        assertFalse(cuenta.isPresent());
    }

    @Test
    void testFindAll() {
        List<Cuenta> cuentas = cuentaRepository.findAll();

        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave() {
        //Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        cuenta = cuentaRepository.save(cuenta);

        //When
        cuenta = cuentaRepository.findById(cuenta.getId()).orElseThrow();

        //Then
        assertEquals("Pepe" , cuenta.getNombrePersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
    }

    @Test
    void testUpdate() {
        //Given
        Cuenta cuenta = new Cuenta(null, "Pepe", new BigDecimal("3000"));
        cuenta = cuentaRepository.save(cuenta);

        //When
        cuenta = cuentaRepository.findById(cuenta.getId()).orElseThrow();

        //Then
        assertEquals("Pepe" , cuenta.getNombrePersona());
        assertEquals("3000", cuenta.getSaldo().toPlainString());
        
        //update
        cuenta.setSaldo(new BigDecimal("3450"));

        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        //Then
        assertEquals("Pepe" , cuentaActualizada.getNombrePersona());
        assertEquals("3450", cuentaActualizada.getSaldo().toPlainString());
    }

    @Test
    void testDelete() {
        Cuenta cuenta = cuentaRepository.findById(2L).orElseThrow();
        assertEquals("Jhon", cuenta.getNombrePersona());

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () -> {
            cuentaRepository.findByNombrePersona("Jhon").orElseThrow();
        });
        assertEquals(1 , cuentaRepository.findAll().size());
    }
}
