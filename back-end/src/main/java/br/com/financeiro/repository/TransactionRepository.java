package br.com.financeiro.repository;

import br.com.financeiro.entity.Transaction;
import br.com.financeiro.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT t FROM Transaction t WHERE t.wallet.id = :walletId AND (:type IS NULL OR t.type = :type) AND (:categoryId IS NULL OR t.category.id = :categoryId) AND (:startDate IS NULL OR t.date >= :startDate) AND (:endDate IS NULL OR t.date <= :endDate)")
    Page<Transaction> listWithFilters(@Param("walletId") Long walletId,
                                     @Param("type") TransactionType type,
                                     @Param("categoryId") Long categoryId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     Pageable pageable);
}