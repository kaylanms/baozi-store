package br.com.baozistore.baozi_store.repository;

import br.com.baozistore.baozi_store.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}