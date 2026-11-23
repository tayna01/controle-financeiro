package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.financeiro.dto.SummaryResponse;
import br.com.financeiro.entity.Category;
import br.com.financeiro.entity.Transaction;
import br.com.financeiro.entity.TransactionType;
import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.TransactionRepository;

@ExtendWith(MockitoExtension.class)
class SummaryServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletAccessService walletAccessService;

    @InjectMocks
    private SummaryService summaryService;

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

    private Transaction transaction(TransactionType type, BigDecimal amount, LocalDate date, Category category) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setCategory(category);
        return transaction;
    }

    private Category category(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

    @Test
    void totalsIncomeExpenseAndBalance() {
        User owner = user(1L);
        Wallet wallet = wallet(owner, 10L);
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 30);

        List<Transaction> transactions = List.of(
                transaction(TransactionType.INCOME, new BigDecimal("100"), LocalDate.of(2026, 9, 5), null),
                transaction(TransactionType.EXPENSE, new BigDecimal("40"), LocalDate.of(2026, 9, 10), null));

        Page<Transaction> page = new PageImpl<>(transactions, Pageable.unpaged(), transactions.size());

        when(walletAccessService.findWalletWithAccess(owner, 10L)).thenReturn(wallet);
        when(transactionRepository.listWithFilters(10L, null, null, start, end, Pageable.unpaged()))
                .thenReturn(page);

        SummaryResponse response = summaryService.summary(owner, 10L, start, end);

        assertEquals(new BigDecimal("100"), response.getTotalIncome());
        assertEquals(new BigDecimal("40"), response.getTotalExpense());
        assertEquals(new BigDecimal("60"), response.getBalance());
        assertEquals(2, response.getTransactionCount());
    }

    @Test
    void groupByCategory() {
        User owner = user(1L);
        Wallet wallet = wallet(owner, 10L);
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 30);

        Category food = category(1L, "Food");
        Category transport = category(2L, "Transport");

        List<Transaction> transactions = List.of(
                transaction(TransactionType.EXPENSE, new BigDecimal("30"), LocalDate.of(2026, 9, 5), food),
                transaction(TransactionType.EXPENSE, new BigDecimal("20"), LocalDate.of(2026, 9, 6), food),
                transaction(TransactionType.EXPENSE, new BigDecimal("15"), LocalDate.of(2026, 9, 7), transport));

        Page<Transaction> page = new PageImpl<>(transactions, Pageable.unpaged(), transactions.size());

        when(walletAccessService.findWalletWithAccess(owner, 10L)).thenReturn(wallet);
        when(transactionRepository.listWithFilters(10L, null, null, start, end, Pageable.unpaged()))
                .thenReturn(page);

        SummaryResponse response = summaryService.summary(owner, 10L, start, end);

        assertEquals(2, response.getByCategory().size());
    }

    @Test
    void groupByMonth() {
        User owner = user(1L);
        Wallet wallet = wallet(owner, 10L);
        LocalDate start = LocalDate.of(2026, 8, 1);
        LocalDate end = LocalDate.of(2026, 9, 30);

        List<Transaction> transactions = List.of(
                transaction(TransactionType.INCOME, new BigDecimal("100"), LocalDate.of(2026, 8, 5), null),
                transaction(TransactionType.EXPENSE, new BigDecimal("40"), LocalDate.of(2026, 9, 10), null));

        Page<Transaction> page = new PageImpl<>(transactions, Pageable.unpaged(), transactions.size());

        when(walletAccessService.findWalletWithAccess(owner, 10L)).thenReturn(wallet);
        when(transactionRepository.listWithFilters(10L, null, null, start, end, Pageable.unpaged()))
                .thenReturn(page);

        SummaryResponse response = summaryService.summary(owner, 10L, start, end);

        assertEquals(2, response.getByMonth().size());
    }

    @Test
    void transactionWithoutCategory() {
        User owner = user(1L);
        Wallet wallet = wallet(owner, 10L);
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 30);

        List<Transaction> transactions = List.of(
                transaction(TransactionType.EXPENSE, new BigDecimal("25"), LocalDate.of(2026, 9, 5), null));

        Page<Transaction> page = new PageImpl<>(transactions, Pageable.unpaged(), transactions.size());

        when(walletAccessService.findWalletWithAccess(owner, 10L)).thenReturn(wallet);
        when(transactionRepository.listWithFilters(10L, null, null, start, end, Pageable.unpaged()))
                .thenReturn(page);

        SummaryResponse response = summaryService.summary(owner, 10L, start, end);

        assertEquals(0, response.getByCategory().size());
        assertEquals(1, response.getTransactionCount());
    }

    @Test
    void emptyPeriodReturnsZeros() {
        User owner = user(1L);
        Wallet wallet = wallet(owner, 10L);
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 30);

        Page<Transaction> page = new PageImpl<>(List.of(), Pageable.unpaged(), 0);

        when(walletAccessService.findWalletWithAccess(owner, 10L)).thenReturn(wallet);
        when(transactionRepository.listWithFilters(10L, null, null, start, end, Pageable.unpaged()))
                .thenReturn(page);

        SummaryResponse response = summaryService.summary(owner, 10L, start, end);

        assertEquals(BigDecimal.ZERO, response.getTotalIncome());
        assertEquals(BigDecimal.ZERO, response.getTotalExpense());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        assertEquals(0, response.getTransactionCount());
        assertEquals(0, response.getByCategory().size());
        assertEquals(0, response.getByMonth().size());
    }

    @Test
    void accessDenied() {
        User outsider = user(99L);

        when(walletAccessService.findWalletWithAccess(outsider, 10L))
                .thenThrow(new ResourceNotFoundException("Wallet not found"));

        assertThrows(ResourceNotFoundException.class,
                () -> summaryService.summary(outsider, 10L, null, null));
    }
}