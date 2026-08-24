package br.com.financeiro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.financeiro.entity.Carteira;

public interface CarteiraRepository extends JpaRepository<Carteira, Long> {

    @Query("SELECT DISTINCT c FROM Carteira c LEFT JOIN c.membros m WHERE c.dono.id = :usuarioId OR m.usuario.id = :usuarioId")
    List<Carteira> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    Optional<Carteira> findByIdAndDonoId(Long id, Long donoId);
}
