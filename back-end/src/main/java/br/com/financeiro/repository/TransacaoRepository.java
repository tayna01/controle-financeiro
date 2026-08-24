package br.com.financeiro.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.financeiro.entity.TipoTransacao;
import br.com.financeiro.entity.Transacao;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    boolean existsByCategoriaId(Long categoriaId);

    @Query("SELECT t FROM Transacao t WHERE t.carteira.id = :carteiraId AND (:tipo IS NULL OR t.tipo = :tipo) AND (:categoriaId IS NULL OR t.categoria.id = :categoriaId) AND (:dataInicio IS NULL OR t.data >= :dataInicio) AND (:dataFim IS NULL OR t.data <= :dataFim)")
    Page<Transacao> listarComFiltros(@Param("carteiraId") Long carteiraId,
                                     @Param("tipo") TipoTransacao tipo,
                                     @Param("categoriaId") Long categoriaId,
                                     @Param("dataInicio") LocalDate dataInicio,
                                     @Param("dataFim") LocalDate dataFim,
                                     Pageable pageable);
}
