package br.com.financeiro.repository;

import br.com.financeiro.entity.WalletMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletMemberRepository extends JpaRepository<WalletMember, Long> {

    Optional<WalletMember> findByWalletIdAndUserId(Long walletId, Long userId);

    boolean existsByWalletIdAndUserId(Long walletId, Long userId);

    List<WalletMember> findByWalletIdOrderByJoinedAtAsc(Long walletId);
}