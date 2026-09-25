package br.com.financeiro;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegracaoApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String novoEmail() {
        return "teste_" + UUID.randomUUID().toString().substring(0, 8) + "@exemplo.com";
    }

    private String registrar(String email) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Teste", "email", email, "password", "SenhaForte@1"))))
                .andExpect(status().isCreated());
        return logar(email);
    }

    private String logar(String email) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "SenhaForte@1"))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString())
                .get("accessToken").asText();
    }

    private JsonNode postComToken(String url, String token, Map<String, Object> corpo) throws Exception {
        MvcResult resultado = mockMvc.perform(post(url)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(corpo)))
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString());
    }

    private JsonNode getComToken(String url, String token) throws Exception {
        MvcResult resultado = mockMvc.perform(get(url)
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString());
    }

    private String json(Object valor) throws Exception {
        return objectMapper.writeValueAsString(valor);
    }

    @Test
    void cadastroLoginEErrosDeAutenticacao() throws Exception {
        String email = novoEmail();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Teste", "email", email, "password", "SenhaForte@1"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Teste", "email", email, "password", "SenhaForte@1"))))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "senha-errada"))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email))))
                .andExpect(status().isBadRequest());

        String token = logar(email);
        getComToken("/api/v1/users/me", token);
        mockMvc.perform(get("/api/v1/users/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void fluxoCarteiraCategoriaETransacao() throws Exception {
        String email = novoEmail();
        String token = registrar(email);

        JsonNode carteira = postComToken("/api/v1/wallets", token, Map.of("name", " Carteira Pessoal "));
        long carteiraId = carteira.get("id").asLong();

        JsonNode categoria = postComToken("/api/v1/categories", token,
                Map.of("name", "Educacao", "type", "EXPENSE", "color", "#663399"));
        long categoriaId = categoria.get("id").asLong();

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Educacao", "type", "EXPENSE"))))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Invalida", "type", "EXPENSE", "color", "azul"))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/wallets/{id}/transactions", carteiraId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("type", "EXPENSE", "amount", 100, "description", "Aluguel",
                                "date", "2026-09-24", "categoryId", categoriaId))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/wallets/{id}/transactions", carteiraId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("type", "EXPENSE", "amount", 0, "description", "Invalida",
                                "date", "2026-09-24"))))
                .andExpect(status().isBadRequest());

        JsonNode summary = getComToken("/api/v1/wallets/" + carteiraId + "/summary", token);
        org.junit.jupiter.api.Assertions.assertEquals(0,
                summary.get("totalExpense").decimalValue().compareTo(new BigDecimal("100")));
        org.junit.jupiter.api.Assertions.assertEquals(1, summary.get("transactionCount").asInt());

        JsonNode lista = getComToken("/api/v1/wallets/" + carteiraId + "/transactions", token);
        org.junit.jupiter.api.Assertions.assertEquals(1, lista.get("totalElements").asInt());
        org.junit.jupiter.api.Assertions.assertEquals("Educacao", lista.get("content").get(0).get("categoryName").asText());
    }

    @Test
    void compartilhamentoEPermissaoPorPapel() throws Exception {
        String emailA = novoEmail();
        String emailB = novoEmail();
        String emailC = novoEmail();

        String tokenA = registrar(emailA);
        String tokenB = registrar(emailB);
        String tokenC = registrar(emailC);

        JsonNode carteira = postComToken("/api/v1/wallets", tokenA, Map.of("name", "Casa"));
        long carteiraId = carteira.get("id").asLong();

        JsonNode membro = postComToken("/api/v1/wallets/" + carteiraId + "/members", tokenA,
                Map.of("email", emailB, "role", "EDITOR"));
        long usuarioB = membro.get("userId").asLong();

        mockMvc.perform(post("/api/v1/wallets/{id}/transactions", carteiraId)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("type", "EXPENSE", "amount", 50, "description", "Feira",
                                "date", "2026-09-24"))))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/api/v1/wallets/{id}/members/{userId}", carteiraId, usuarioB)
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("role", "VISUALIZADOR"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/wallets/{id}/transactions", carteiraId)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("type", "EXPENSE", "amount", 60, "description", "Bloqueada",
                                "date", "2026-09-24"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/wallets/{id}", carteiraId)
                        .header("Authorization", "Bearer " + tokenC))
                .andExpect(status().isNotFound());
    }

    @Test
    void cabecalhosDeSegurancaPresentes() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Frame-Options", "DENY"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Content-Security-Policy",
                        org.hamcrest.Matchers.containsString("frame-ancestors 'none'")));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}