package br.com.financeiro.repository;

import br.com.financeiro.entity.TokenRedefinicaoSenha;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRedefinicaoSenhaRepository extends JpaRepository<TokenRedefinicaoSenha, Long> {
}
