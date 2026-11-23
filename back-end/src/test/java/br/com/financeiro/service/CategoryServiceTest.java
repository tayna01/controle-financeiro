package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.financeiro.dto.CategoryRequest;
import br.com.financeiro.entity.Category;
import br.com.financeiro.entity.TransactionType;
import br.com.financeiro.entity.User;
import br.com.financeiro.exception.BusinessException;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.repository.CategoryRepository;
import br.com.financeiro.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("Ana");
        user.setEmail("ana@example.com");
        return user;
    }

    @Test
    void createDuplicateNameThrowsConflict() {
        when(categoryRepository.existsByUserIdAndNameIgnoreCase(1L, "Food", null)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoryService.create(user(1L),
                new CategoryRequest(" Food ", TransactionType.EXPENSE, "#ff9900", null)));

        verify(categoryRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void createTrimsNameAndColor() {
        when(categoryRepository.existsByUserIdAndNameIgnoreCase(1L, "Transport", null)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        categoryService.create(user(1L),
                new CategoryRequest("  Transport  ", TransactionType.EXPENSE, " #00aaff ", null));

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("Transport", captor.getValue().getName());
        assertEquals("#00aaff", captor.getValue().getColor());
        assertEquals(TransactionType.EXPENSE, captor.getValue().getType());
    }

    @Test
    void deleteCategoryWithTransactionsThrows() {
        Category category = new Category();
        category.setId(9L);

        when(categoryRepository.findByIdAndUserId(9L, 1L)).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategoryId(9L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> categoryService.delete(user(1L), 9L));

        verify(categoryRepository, org.mockito.Mockito.never()).delete(any());
    }

    @Test
    void listWithNullTypeDoesNotFilterByType() {
        categoryService.list(user(1L), null);
        verify(categoryRepository).findByUserIdOrderByNameAsc(eq(1L));
    }
}