package org.hhidalgo.appmockito.ejemplos.springboot.test.repository;

import org.hhidalgo.appmockito.ejemplos.springboot.test.model.Banco;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BancoRepository extends JpaRepository<Banco, Long> {
}
