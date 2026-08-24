package br.com.financeiro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.financeiro.entity.TokenRedefinicaoSenha;

public interface TokenRedefinicaoSenhaRepository extends JpaRepository<TokenRedefinicaoSenha, Long> {

    Optional<TokenRedefinicaoSenha> findByToken(String token);

    List<TokenRedefinicaoSenha> findByUsuarioIdAndUtilizadoFalse(Long usuarioId);
}
