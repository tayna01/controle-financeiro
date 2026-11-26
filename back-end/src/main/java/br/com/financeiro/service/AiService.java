package br.com.financeiro.service;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.financeiro.ai.AiClient;
import br.com.financeiro.dto.CategoryResponse;
import br.com.financeiro.dto.SummaryResponse;
import br.com.financeiro.entity.Category;
import br.com.financeiro.entity.User;
import br.com.financeiro.repository.CategoryRepository;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

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
        try {
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
        } catch (Exception ex) {
            log.error("Falha na categorizacao por IA", ex);
            return null;
        }
    }

    public String insights(User user, Long walletId, LocalDate startDate, LocalDate endDate) {
        if (!aiClient.isAvailable()) {
            return null;
        }
        try {
            walletAccessService.findWalletWithAccess(user, walletId);
            SummaryResponse summary = summaryService.summary(user, walletId, startDate, endDate);

            StringBuilder aggregated = new StringBuilder();
            aggregated.append("Receitas: ").append(summary.getTotalIncome()).append("\n");
            aggregated.append("Despesas: ").append(summary.getTotalExpense()).append("\n");
            aggregated.append("Saldo: ").append(summary.getBalance()).append("\n");
            aggregated.append("Quantidade de transacoes: ").append(summary.getTransactionCount()).append("\n");
            aggregated.append("Por categoria:\n");
            for (SummaryResponse.CategorySummary item : summary.getByCategory()) {
                aggregated.append("- ").append(item.getCategoryName()).append(": ").append(item.getTotal()).append("\n");
            }

            return aiClient.insights(aggregated.toString());
        } catch (Exception ex) {
            log.error("Falha ao gerar insights por IA", ex);
            return null;
        }
    }
}