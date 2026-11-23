package br.com.financeiro.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.financeiro.dto.SummaryResponse;
import br.com.financeiro.entity.Transaction;
import br.com.financeiro.entity.TransactionType;
import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.repository.TransactionRepository;

@Service
@Transactional(readOnly = true)
public class SummaryService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletAccessService walletAccessService;

    public SummaryResponse summary(User user, Long walletId, LocalDate startDate, LocalDate endDate) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        Page<Transaction> page = transactionRepository.listWithFilters(
                wallet.getId(), null, null, startDate, endDate, Pageable.unpaged());
        List<Transaction> transactions = page.getContent();

        SummaryResponse response = new SummaryResponse();
        Map<Long, SummaryResponse.CategorySummary> byCategory = new HashMap<>();
        Map<String, SummaryResponse.MonthSummary> byMonth = new TreeMap<>();

        for (Transaction transaction : transactions) {
            boolean income = transaction.getType() == TransactionType.INCOME;

            if (income) {
                response.setTotalIncome(response.getTotalIncome().add(transaction.getAmount()));
            } else {
                response.setTotalExpense(response.getTotalExpense().add(transaction.getAmount()));
            }
            response.setTransactionCount(response.getTransactionCount() + 1);

            if (transaction.getCategory() != null) {
                Long categoryId = transaction.getCategory().getId();
                SummaryResponse.CategorySummary item = byCategory.get(categoryId);
                if (item == null) {
                    item = new SummaryResponse.CategorySummary(categoryId, transaction.getCategory().getName(), BigDecimal.ZERO);
                    byCategory.put(categoryId, item);
                }
                item.setTotal(item.getTotal().add(transaction.getAmount()));
            }

            String key = String.format("%04d-%02d", transaction.getDate().getYear(), transaction.getDate().getMonthValue());
            SummaryResponse.MonthSummary month = byMonth.get(key);
            if (month == null) {
                month = new SummaryResponse.MonthSummary(key, BigDecimal.ZERO, BigDecimal.ZERO);
                byMonth.put(key, month);
            }
            if (income) {
                month.setIncome(month.getIncome().add(transaction.getAmount()));
            } else {
                month.setExpense(month.getExpense().add(transaction.getAmount()));
            }
        }

        response.setBalance(response.getTotalIncome().subtract(response.getTotalExpense()));
        response.getByCategory().addAll(byCategory.values());
        response.getByMonth().addAll(byMonth.values());
        return response;
    }
}