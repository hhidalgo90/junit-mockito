package org.hhidalgo.appmockito.ejemplos.springboot.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Cuenta;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.TransferenciaDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase que va a consumir api rest de forma real, pruebas de integracion.
 */
@Tag("integracion_web_client")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//indica el orden en que se ejecutan los metodos mediante anotacion
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)//corre aplicacion en puerto aleatorio.
class CuentaControllerWebClientTest {

    private ObjectMapper mapper;

    @Autowired
    private WebTestClient testClient;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void test_transferir() throws JsonProcessingException {
        TransferenciaDTO datos = new TransferenciaDTO();
        datos.setCuentaOrigenId(1L);
        datos.setCuentaDestinoId(2L);
        datos.setBancoId(1L);
        datos.setMonto(new BigDecimal("200"));

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", datos);

        testClient.post().uri("http://localhost:8080/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(datos)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.mensaje").isNotEmpty()
                .jsonPath("$.mensaje").value(is("Transferencia realizada con exito"))
                .jsonPath("$.mensaje").value(valor -> assertEquals("Transferencia realizada con exito", valor))
                .jsonPath("$.mensaje").isEqualTo("Transferencia realizada con exito")
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(datos.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(mapper.writeValueAsString(response));
    }

    @Test
    @Order(2)
    void test_detalle() throws JsonProcessingException {
        Cuenta cuentaEsperada = new Cuenta(1l, "Andres" , new BigDecimal("800"));

        testClient.get().uri("http://localhost:8080/api/cuentas/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nombrePersona").value(is("Andres"))
                .jsonPath("$.saldo").isEqualTo(800)
                .json(mapper.writeValueAsString(cuentaEsperada));
    }

    @Test
    @Order(3)
    void test_detalle2() {
        testClient.get().uri("http://localhost:8080/api/cuentas/2")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(resp -> {
                    Cuenta cuenta = resp.getResponseBody();
                    assertEquals("Jhon", cuenta.getNombrePersona());
                    assertEquals("1200.00", cuenta.getSaldo().toPlainString());
                });
    }

    @Test
    @Order(4)
    void test_findAll() {
        testClient.get().uri("http://localhost:8080/api/cuentas")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].nombrePersona").value(is("Andres"))
                .jsonPath("$[1].nombrePersona").value(is("Jhon"))
                .jsonPath("$[0].saldo").isEqualTo(800)
                .jsonPath("$[1].saldo").isEqualTo(1200)
                .jsonPath("$").isArray()
                .jsonPath("$", hasSize(3));
    }

    @Test
    @Order(5)
    void test_findAll2() {
        testClient.get().uri("http://localhost:8080/api/cuentas")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)//esperamos una lista de Cuentas
                .consumeWith(resp -> { //alternativa al jsonPath usando exp lambdas
                    List<Cuenta> cuentas = resp.getResponseBody();
                    assertNotNull(cuentas);
                    assertEquals(2, cuentas.size());
                    assertEquals(1L, cuentas.get(0).getId());
                    assertEquals("Andres", cuentas.get(0).getNombrePersona());
                    assertEquals("800.0", cuentas.get(0).getSaldo().toPlainString());

                    assertEquals(2L, cuentas.get(1).getId());
                    assertEquals("Jhon", cuentas.get(1).getNombrePersona());
                    assertEquals("1200.0", cuentas.get(1).getSaldo().toPlainString());
                })
                .hasSize(2);
    }

    @Test
    @Order(6)
    void test_save() {
        //Given
        Cuenta datos = new Cuenta(3L, "Hector", new BigDecimal("2000"));


        testClient.post().uri("http://localhost:8080/api/cuentas")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(datos)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3L)
                .jsonPath("$.nombrePersona").value(is("Hector"))
                .jsonPath("$.saldo").isEqualTo(2000);
    }

    @Test
    @Order(7)
    void test_eliminar() {
        testClient.get().uri("http://localhost:8080/api/cuentas").exchange()
                        .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                        .expectBodyList(Cuenta.class).consumeWith(resp -> {
                    System.out.println(resp);
                })
                                .hasSize(3);


        testClient.delete().uri("http://localhost:8080/api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        testClient.get().uri("http://localhost:8080/api/cuentas").exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .hasSize(2);

        testClient.get().uri("http://localhost:8080/api/cuentas/3").exchange()
                .expectStatus().isNotFound().expectBody().isEmpty();
    }
}