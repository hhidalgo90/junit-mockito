package org.hhidalgo.appmockito.ejemplos.springboot.test.model;

import javax.persistence.*;

@Entity
@Table(name = "bancos")
public class Banco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre")
    private String nombreBanco;
    @Column(name = "total_transferencias")
    private int totalTransferencias;

    public Banco() {
    }

    public Banco(Long id, String nombreBanco, int totalTransferencias) {
        this.id = id;
        this.nombreBanco = nombreBanco;
        this.totalTransferencias = totalTransferencias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public int getTotalTransferencias() {
        return totalTransferencias;
    }

    public void setTotalTransferencias(int totalTransferencias) {
        this.totalTransferencias = totalTransferencias;
    }
}
