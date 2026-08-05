package br.com.financeiro.repository;

import br.com.financeiro.entity.Categoria;
import br.com.financeiro.entity.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioId(Long usuarioId);

    List<Categoria> findByUsuarioIdAndTipo(Long usuarioId, TipoTransacao tipo);
}
