package br.com.financeiro.repository;

import br.com.financeiro.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("SELECT DISTINCT w FROM Wallet w LEFT JOIN w.members m WHERE w.owner.id = :userId OR m.user.id = :userId")
    List<Wallet> findByUserId(@Param("userId") Long userId);

    Optional<Wallet> findByIdAndOwnerId(Long id, Long ownerId);
}