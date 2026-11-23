package br.com.financeiro.repository;

import br.com.financeiro.entity.Category;
import br.com.financeiro.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserIdOrderByNameAsc(Long userId);

    List<Category> findByUserIdAndTypeOrderByNameAsc(Long userId, TransactionType type);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.user.id = :userId AND LOWER(c.name) = LOWER(:name) AND (:id IS NULL OR c.id <> :id)")
    boolean existsByUserIdAndNameIgnoreCase(@Param("userId") Long userId,
                                           @Param("name") String name,
                                           @Param("id") Long idToIgnore);
}