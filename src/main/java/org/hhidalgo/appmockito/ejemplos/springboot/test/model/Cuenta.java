package org.hhidalgo.appmockito.ejemplos.springboot.test.model;

import org.hhidalgo.appmockito.ejemplos.springboot.test.exception.DineroInsuficienteException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cuentas")
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre")
    private String nombrePersona;
    private BigDecimal saldo;

    public Cuenta() {
    }

    public Cuenta(Long id, String nombrePersona, BigDecimal saldo) {
        this.id = id;
        this.nombrePersona = nombrePersona;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void debito(BigDecimal monto){
        BigDecimal nuevoSaldo = this.saldo.subtract(monto);
        System.out.println("Debito: " + this.saldo.toPlainString());
        if(nuevoSaldo.compareTo(BigDecimal.ZERO) < 0){
            System.out.println("Debito DineroInsuficienteException: ");
            throw new DineroInsuficienteException("aweonao no te quedan moneas");
        }
        this.saldo = nuevoSaldo;
    }

    public void credito(BigDecimal monto){
        this.saldo = this.saldo.add(monto);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta cuenta = (Cuenta) o;
        return Objects.equals(id, cuenta.id) && Objects.equals(nombrePersona, cuenta.nombrePersona) && Objects.equals(saldo, cuenta.saldo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombrePersona, saldo);
    }
}
