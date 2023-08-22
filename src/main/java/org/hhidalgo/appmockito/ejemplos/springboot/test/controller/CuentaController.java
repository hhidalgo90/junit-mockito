package org.hhidalgo.appmockito.ejemplos.springboot.test.controller;

import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Cuenta;
import org.hhidalgo.appmockito.ejemplos.springboot.test.model.TransferenciaDTO;
import org.hhidalgo.appmockito.ejemplos.springboot.test.service.CuentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){
        Cuenta cuenta = null;
        try {
            cuenta = cuentaService.findById(id);
        } catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cuenta);
    }

    @PostMapping("/transferir")
    public ResponseEntity<?> transferir(@RequestBody TransferenciaDTO datos){
        cuentaService.transferir(datos.getCuentaOrigenId(), datos.getCuentaDestinoId(), datos.getMonto(), datos.getBancoId());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito");
        response.put("transaccion", datos);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Cuenta> findAll(){
        return cuentaService.findAll();
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cuenta save(@RequestBody Cuenta cuenta){
        return cuentaService.save(cuenta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id){
        System.out.println("deleteById " + id);
        cuentaService.deleteById(id);
    }
}
