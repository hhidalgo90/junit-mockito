package org.hhidalgo.appmockito.ejemplos.springboot.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Cuenta;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.TransferenciaDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integracion_rest_template")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerTestRestTemplate {

    @Autowired
    private TestRestTemplate restTemplate;

    private ObjectMapper mapper;

    @LocalServerPort
    private int puerto;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    private String crearUri(String uri){
        return "http://localhost:"+puerto+uri;
    }

    @Test
    @Order(1)
    void test_transferir() throws JsonProcessingException {
        TransferenciaDTO datos = new TransferenciaDTO();
        datos.setCuentaOrigenId(1L);
        datos.setCuentaDestinoId(2L);
        datos.setBancoId(1L);
        datos.setMonto(new BigDecimal("200"));

        System.out.println(puerto);

        ResponseEntity<String> responseEntity = restTemplate.
                postForEntity(crearUri("/api/cuentas/transferir"), datos, String.class);

        String json = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con exito"));

        JsonNode jsonNode = mapper.readTree(json);

        assertEquals("Transferencia realizada con exito", jsonNode.path("mensaje").asText());
        assertEquals(LocalDate.now().toString(), jsonNode.path("date").asText());
        assertEquals("200", jsonNode.path("transaccion").path("monto").asText());
        assertEquals(1L, jsonNode.path("transaccion").path("cuentaOrigenId").asLong());
    }

    @Test
    @Order(2)
    void test_findById() {
        ResponseEntity<Cuenta> response = restTemplate.getForEntity(crearUri("/api/cuentas/1"), Cuenta.class);

        Cuenta cuenta = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        assertEquals("Andres", cuenta.getNombrePersona());
        assertEquals("800.00", cuenta.getSaldo().toPlainString());
        assertEquals(new Cuenta(1L, "Andres", new BigDecimal("800.00")), cuenta);
    }

    @Test
    @Order(3)
    void testFindAll() throws JsonProcessingException {
        ResponseEntity<Cuenta[]> response = restTemplate.getForEntity(crearUri("/api/cuentas/"), Cuenta[].class);

        List<Cuenta> cuentas = Arrays.asList(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

        assertEquals("Andres", cuentas.get(0).getNombrePersona());
        assertEquals("800.00", cuentas.get(0).getSaldo().toPlainString());
        assertEquals(1L, cuentas.get(0).getId());
        assertEquals("Jhon", cuentas.get(1).getNombrePersona());
        assertEquals("1200.00", cuentas.get(1).getSaldo().toPlainString());
        assertEquals(2L, cuentas.get(1).getId());

        //Probar con JsonNode

        JsonNode jsonNode = mapper.readTree(mapper.writeValueAsString(cuentas));
        assertEquals("Andres", jsonNode.get(0).path("nombrePersona").asText());
        assertEquals("800.0", jsonNode.get(0).path("saldo").asText());
        assertEquals(1L, jsonNode.get(0).path("id").asLong());
    }

    @Test
    @Order(4)
    void test_save() {
        ResponseEntity<Cuenta> andres = restTemplate.postForEntity(crearUri("/api/cuentas/"), new Cuenta(null, "Andres", new BigDecimal("5000")), Cuenta.class);

        assertEquals(HttpStatus.CREATED, andres.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, andres.getHeaders().getContentType());
        assertNotNull(andres.getBody());
        assertEquals("Andres", andres.getBody().getNombrePersona());
        assertEquals("5000", andres.getBody().getSaldo().toPlainString());
        assertEquals(3L, andres.getBody().getId());
    }

    @Test
    @Order(5)
    void test_delete() {
        ResponseEntity<Cuenta[]> response = restTemplate.getForEntity(crearUri("/api/cuentas/"), Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, cuentas.size());


        //restTemplate.delete(crearUri("/api/cuentas/2"));

        Map<String, Long> parametros = new HashMap<>();
        parametros.put("id", 2L);
        ResponseEntity<Void> exchange = restTemplate.exchange(crearUri("/api/cuentas/{id}"), HttpMethod.DELETE, null, Void.class, parametros);
        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertNull(exchange.getBody());
        assertFalse(exchange.hasBody());

        response = restTemplate.getForEntity(crearUri("/api/cuentas/"), Cuenta[].class);
        cuentas = Arrays.asList(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, cuentas.size());


        ResponseEntity<Cuenta> respDetalle = restTemplate.getForEntity(crearUri("/api/cuentas/2"), Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND, respDetalle.getStatusCode());
        assertFalse(respDetalle.hasBody());
    }
}