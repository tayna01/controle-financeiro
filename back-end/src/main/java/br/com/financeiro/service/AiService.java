package br.com.financeiro.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.financeiro.ai.AiClient;
import br.com.financeiro.dto.CategoryResponse;
import br.com.financeiro.entity.Category;
import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.repository.CategoryRepository;

@Service
public class AiService {

    @Autowired
    private AiClient aiClient;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private WalletAccessService walletAccessService;

    public CategoryResponse categorize(User user, String description) {
        if (!aiClient.isAvailable() || description == null || description.isBlank()) {
            return null;
        }

        List<Category> categories = categoryRepository.findByUserIdOrderByNameAsc(user.getId());
        if (categories.isEmpty()) {
            return null;
        }

        Long categoryId = aiClient.categorize(description, categories);
        if (categoryId == null) {
            return null;
        }

        return categories.stream()
                .filter(category -> category.getId().equals(categoryId))
                .findFirst()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getType().name(),
                        category.getColor(),
                        category.getIcon(),
                        category.getCreatedAt()))
                .orElse(null);
    }

    public String insights(User user, Long walletId, LocalDate startDate, LocalDate endDate) {
        if (!aiClient.isAvailable()) {
            return null;
        }

        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        br.com.financeiro.dto.SummaryResponse summary = summaryService.summary(user, walletId, startDate, endDate);

        StringBuilder aggregated = new StringBuilder();
        aggregated.append("Receitas: ").append(summary.getTotalIncome()).append("\n");
        aggregated.append("Despesas: ").append(summary.getTotalExpense()).append("\n");
        aggregated.append("Saldo: ").append(summary.getBalance()).append("\n");
        aggregated.append("Quantidade de transacoes: ").append(summary.getTransactionCount()).append("\n");
        aggregated.append("Por categoria:\n");
        for (br.com.financeiro.dto.SummaryResponse.CategorySummary item : summary.getByCategory()) {
            aggregated.append("- ").append(item.getCategoryName()).append(": ").append(item.getTotal()).append("\n");
        }

        return aiClient.insights(aggregated.toString());
    }
}