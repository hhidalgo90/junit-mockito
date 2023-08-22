package org.hhidalgo.appmockito.ejemplos.springboot.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.ContentType;
import org.hhidalgo.appmockito.ejemplos.springboot.test.Datos;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Cuenta;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.TransferenciaDTO;
import org.hhidalgo.appmockito.ejemplos.springboot.test.service.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Anotacion que indica que se va a probar un controllador rest.
 */
@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    /**
     * Clase para probar un controlador rest en spring. Simula los request y responses http y el servidor.
     */
    @Autowired
    private MockMvc mvc;

    @MockBean
    private CuentaService cuentaService;

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        Datos.CUENTA_001.get().setSaldo(new BigDecimal("1000"));
        Datos.CUENTA_002.get().setSaldo(new BigDecimal("2000"));
        Datos.BANCO.get().setTotalTransferencias(0);
    }

    @Test
    void detalle() throws Exception {
        //Given
        when(cuentaService.findById(1L)).thenReturn(Datos.CUENTA_001.orElseThrow());

        //When
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))

                //then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nombrePersona").value("Andres"))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(cuentaService).findById(1L);
    }

    @Test
    void transferir() throws Exception {
        //Given
        TransferenciaDTO transferencia = new TransferenciaDTO();
        transferencia.setCuentaOrigenId(1L);
        transferencia.setCuentaDestinoId(2L);
        transferencia.setMonto(new BigDecimal("100"));
        transferencia.setBancoId(1L);
        System.out.println(mapper.writeValueAsString(transferencia));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", transferencia);

        System.out.println(mapper.writeValueAsString(response));

        //When

        mvc.perform(MockMvcRequestBuilders.post("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(transferencia)))

                //Then

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con exito"))
                .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(transferencia.getCuentaOrigenId()))
                .andExpect(content().json(mapper.writeValueAsString(response)));
    }

    @Test
    void testListar() throws Exception {
        List<Cuenta> cuentas = Arrays.asList(Datos.CUENTA_001.orElseThrow(), Datos.CUENTA_002.orElseThrow());
        when(cuentaService.findAll()).thenReturn(cuentas);

        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nombrePersona").value("Andres"))
                .andExpect(jsonPath("$[1].nombrePersona").value("Hector"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(mapper.writeValueAsString(cuentas)));

    }

    @Test
    void test_guardar() throws Exception {
        Cuenta cuenta = Datos.CUENTA_001.orElseThrow();

        when(cuentaService.save(cuenta)).then(arg -> {
            Cuenta c = arg.getArgument(0);
            c.setId(3L);
            return c;
        });

        mvc.perform(MockMvcRequestBuilders.post("/api/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(cuenta)))

                //Then

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.nombrePersona").value("Andres"))
                .andExpect(jsonPath("$.saldo").value("1000"));

    }
}