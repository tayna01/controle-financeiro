package br.com.financeiro.repository;

import br.com.financeiro.entity.CarteiraMembro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarteiraMembroRepository extends JpaRepository<CarteiraMembro, Long> {
}
