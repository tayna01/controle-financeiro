package br.com.financeiro.repository;

import br.com.financeiro.entity.Categoria;
import br.com.financeiro.entity.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioIdOrderByNomeAsc(Long usuarioId);

    List<Categoria> findByUsuarioIdAndTipoOrderByNomeAsc(Long usuarioId, TipoTransacao tipo);

    Optional<Categoria> findByIdAndUsuarioId(Long id, Long usuarioId);

    @Query("""
            SELECT COUNT(c) > 0
            FROM Categoria c
            WHERE c.usuario.id = :usuarioId
              AND LOWER(c.nome) = LOWER(:nome)
              AND (:id IS NULL OR c.id <> :id)
            """)
    boolean existsByUsuarioIdAndNomeIgnoreCase(@Param("usuarioId") Long usuarioId,
                                               @Param("nome") String nome,
                                               @Param("id") Long idIgnorar);
}
