package org.hhidalgo.appmockito.ejemplos.springboot.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.hhidalgo.appmockito.ejemplos.springboot.test.exception.DineroInsuficienteException;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Banco;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Cuenta;
import org.hhidalgo.appmockito.ejemplos.springboot.test.repository.BancoRepository;
import org.hhidalgo.appmockito.ejemplos.springboot.test.repository.CuentaRepository;
import org.hhidalgo.appmockito.ejemplos.springboot.test.service.CuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SpringbootTestApplicationTests {

	@MockBean
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;
	@Autowired
	CuentaService service;

	@BeforeEach
	void setUp() {
		//cuentaRepository = mock(CuentaRepository.class);
		//bancoRepository = mock(BancoRepository.class);
		//service = new CuentaServiceImpl(cuentaRepository,bancoRepository);
		Datos.CUENTA_001.get().setSaldo(new BigDecimal("1000"));
		Datos.CUENTA_002.get().setSaldo(new BigDecimal("2000"));
		Datos.BANCO.get().setTotalTransferencias(0);
	}

	@Test
	void contextLoads() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001);
		when(cuentaRepository.findById(2L)).thenReturn(Datos.CUENTA_002);
		when(bancoRepository.findById(1L)).thenReturn(Datos.BANCO);

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		service.transferir(1L, 2L , new BigDecimal(100), 1L);

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		int total = service.revisarTotalTransferencia(1L);
		assertEquals(1 , total);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);

		verify(cuentaRepository, times(2)).save(any(Cuenta.class));

		//Por defecto, times es 1
		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, never()).findAll();
		verify(cuentaRepository, times(6)).findById(anyLong());
	}

	/**
	 * Se agrega verificacion de transferencia mayor al monto disponible
	 * Se usa manejo de excepcion.
	 */
	@Test
	void contextLoads2() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001);
		when(cuentaRepository.findById(2L)).thenReturn(Datos.CUENTA_002);
		when(bancoRepository.findById(1L)).thenReturn(Datos.BANCO);

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class,()->{
			service.transferir(1L, 2L , new BigDecimal("1200"), 1L);
		});

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		int total = service.revisarTotalTransferencia(1L);
		assertEquals(0 , total);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);

		verify(cuentaRepository, never()).save(any(Cuenta.class));

		//Por defecto, times es 1
		verify(bancoRepository, times(1)).findById(1L);
		verify(bancoRepository, never()).save(any(Banco.class));
	}

	@Test
	void contextLoads3() {
		when(cuentaRepository.findById(1L)).thenReturn(Datos.CUENTA_001);

		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);

		assertSame(cuenta1, cuenta2);// los dos objetos sean iguales
		assertTrue(cuenta1 == cuenta2);
		assertEquals("Andres", cuenta1.getNombrePersona());
		assertEquals("Andres", cuenta2.getNombrePersona());

		verify(cuentaRepository, times(2)).findById(1L);
	}

	@Test
	void test_find_all() {
		List<Cuenta> cuentas = Arrays.asList(Datos.CUENTA_001.orElseThrow(), Datos.CUENTA_002.orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(cuentas);

		List<Cuenta> listaCuentas = service.findAll();

		assertFalse(listaCuentas.isEmpty());
		assertEquals(2, listaCuentas.size());
		assertTrue(cuentas.contains(Datos.CUENTA_001.orElseThrow()));

		verify(cuentaRepository).findAll();
	}

	@Test
	void test_save() {
		Cuenta cuenta = Datos.CUENTA_001.orElseThrow();
		when(cuentaRepository.save(any())).then(resp -> {
			Cuenta c = resp.getArgument(0);
			c.setId(3L);
			return c;
		});

		Cuenta cuenta2 = service.save(cuenta);

		assertTrue(cuenta2 != null);
		assertEquals("Andres", cuenta2.getNombrePersona());
		assertEquals("1000", cuenta2.getSaldo().toPlainString());
		assertEquals(3, cuenta2.getId());

		verify(cuentaRepository).save(cuenta);
	}
}
