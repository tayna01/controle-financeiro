package br.com.financeiro.ai;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.financeiro.entity.Category;

@Component
public class GeminiAiClient implements AiClient {

    @Value("${gemini.api-key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String model;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public Long categorize(String description, List<Category> categories) {
        if (!isAvailable() || description == null || description.isBlank() || categories.isEmpty()) {
            return null;
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("Escolha o id da categoria mais adequada para a descricao: \"")
                .append(description).append("\".\n");
        prompt.append("Categorias disponiveis:\n");
        for (Category category : categories) {
            prompt.append(category.getId()).append(": ").append(category.getName())
                    .append(" (").append(category.getType().name()).append(")\n");
        }
        prompt.append("Responda apenas com o id numerico da categoria ou null.");

        String response = callGemini(prompt.toString(), 64);
        if (response == null) {
            return null;
        }
        String trimmed = response.trim();
        if (trimmed.equalsIgnoreCase("null")) {
            return null;
        }
        try {
            return Long.valueOf(trimmed.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    public String insights(String aggregatedSummary) {
        if (!isAvailable() || aggregatedSummary == null || aggregatedSummary.isBlank()) {
            return null;
        }

        String prompt = "Voce e um assistente financeiro. Com base no resumo agregado abaixo, "
                + "forneca um insight curto em portugues (maximo 3 frases) sobre os gastos e receitas. "
                + "Nao invente dados.\n\n" + aggregatedSummary;

        return callGemini(prompt, 256);
    }

    private String callGemini(String prompt, int maxTokens) {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + model + ":generateContent?key=" + apiKey;

            String body = objectMapper.writeValueAsString(new java.util.LinkedHashMap<String, Object>() {{
                put("contents", java.util.List.of(
                        new java.util.LinkedHashMap<String, Object>() {{
                            put("parts", java.util.List.of(
                                    new java.util.LinkedHashMap<String, Object>() {{
                                        put("text", prompt);
                                    }}
                            ));
                        }}
                ));
                put("generationConfig", new java.util.LinkedHashMap<String, Object>() {{
                    put("temperature", 0.2);
                    put("maxOutputTokens", maxTokens);
                }});
            }});

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return null;
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            if (text.isMissingNode() || text.asText().isBlank()) {
                return null;
            }
            return text.asText().trim();
        } catch (Exception ex) {
            return null;
        }
    }
}