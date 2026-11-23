package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import br.com.financeiro.dto.TransactionRequest;
import br.com.financeiro.entity.TransactionType;
import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CategoryRepository;
import br.com.financeiro.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private WalletAccessService walletAccessService;

    @InjectMocks
    private TransactionService transactionService;

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }

    private Wallet wallet(User owner, Long id) {
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setOwner(owner);
        wallet.setName("Wallet");
        return wallet;
    }

    private TransactionRequest request(BigDecimal amount) {
        return new TransactionRequest(TransactionType.EXPENSE, amount, "Test",
                LocalDate.now(), null);
    }

    @Test
    void viewerCannotCreateTransaction() {
        User owner = user(1L);
        User viewer = user(2L);
        Wallet wallet = wallet(owner, 10L);

        when(walletAccessService.findWalletWithAccess(viewer, 10L)).thenReturn(wallet);
        org.mockito.Mockito.doThrow(new AccessDeniedException("You do not have permission to change transactions in this wallet"))
                .when(walletAccessService).requireWritePermission(viewer, wallet);

        assertThrows(AccessDeniedException.class,
                () -> transactionService.create(viewer, 10L, request(new BigDecimal("50"))));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void outsiderCannotListTransactions() {
        User owner = user(1L);
        User outsider = user(99L);
        Wallet wallet = wallet(owner, 10L);

        when(walletAccessService.findWalletWithAccess(outsider, 10L))
                .thenThrow(new ResourceNotFoundException("Wallet not found"));

        assertThrows(ResourceNotFoundException.class, () -> transactionService
                .list(outsider, 10L, null, null, null, null, Pageable.unpaged()));
    }

    @Test
    void editorCanCreateTransaction() {
        User owner = user(1L);
        User editor = user(2L);
        Wallet wallet = wallet(owner, 10L);

        when(walletAccessService.findWalletWithAccess(editor, 10L)).thenReturn(wallet);
        when(transactionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transactionService.create(editor, 10L, request(new BigDecimal("50")));

        verify(transactionRepository).save(any());
    }
}