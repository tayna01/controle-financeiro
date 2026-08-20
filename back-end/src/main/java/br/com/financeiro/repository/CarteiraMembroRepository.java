package br.com.financeiro.repository;

import br.com.financeiro.entity.CarteiraMembro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarteiraMembroRepository extends JpaRepository<CarteiraMembro, Long> {

    Optional<CarteiraMembro> findByCarteiraIdAndUsuarioId(Long carteiraId, Long usuarioId);

    boolean existsByCarteiraIdAndUsuarioId(Long carteiraId, Long usuarioId);

    List<CarteiraMembro> findByCarteiraIdOrderByEntradoEmAsc(Long carteiraId);
}