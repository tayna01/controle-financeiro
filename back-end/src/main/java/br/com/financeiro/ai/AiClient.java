package br.com.financeiro.ai;

import java.util.List;

import br.com.financeiro.entity.Category;

public interface AiClient {

    boolean isAvailable();

    Long categorize(String description, List<Category> categories);

    String insights(String aggregatedSummary);
}