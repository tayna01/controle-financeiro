package br.com.financeiro.repository;

import br.com.financeiro.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    boolean existsByCategoriaId(Long categoriaId);
}
