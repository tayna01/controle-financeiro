package br.com.financeiro;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class BrokenAccessControlTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String novoEmail() {
        return "teste_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private String registrarELogar(String email) throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Teste", "email", email, "password", "StrongPass@1"))))
                .andExpect(status().isCreated());

        MvcResult resultado = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "StrongPass@1"))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString())
                .get("accessToken").asText();
    }

    @Test
    void usuarioBNaoLeDadosDoUsuarioA() throws Exception {
        String emailA = novoEmail();
        String emailB = novoEmail();
        String tokenA = registrarELogar(emailA);
        String tokenB = registrarELogar(emailB);

        MvcResult carteiraResult = mockMvc.perform(post("/api/v1/wallets")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Carteira A"))))
                .andExpect(status().isCreated())
                .andReturn();
        long walletId = objectMapper.readTree(carteiraResult.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(post("/api/v1/wallets/" + walletId + "/transactions")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("type", "EXPENSE", "amount", 10, "description", "Teste",
                                "date", "2026-09-24"))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/wallets/" + walletId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/v1/wallets/" + walletId + "/transactions")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());

        MvcResult categoriasB = mockMvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = objectMapper.readTree(categoriasB.getResponse().getContentAsString());
        org.junit.jupiter.api.Assertions.assertTrue(node.isArray());
    }

    @Test
    void viewerNaoPodeEscrever() throws Exception {
        String emailA = novoEmail();
        String emailB = novoEmail();
        String tokenA = registrarELogar(emailA);
        String tokenB = registrarELogar(emailB);

        MvcResult carteiraResult = mockMvc.perform(post("/api/v1/wallets")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("name", "Carteira A"))))
                .andExpect(status().isCreated())
                .andReturn();
        long walletId = objectMapper.readTree(carteiraResult.getResponse().getContentAsString()).get("id").asLong();

        MvcResult membroResult = mockMvc.perform(post("/api/v1/wallets/" + walletId + "/members")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", emailB, "role", "VIEWER"))))
                .andExpect(status().isCreated())
                .andReturn();
        // ensure viewer cannot create transaction
        mockMvc.perform(post("/api/v1/wallets/" + walletId + "/transactions")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("type", "EXPENSE", "amount", 10, "description", "Bloqueada",
                                "date", "2026-09-24"))))
                .andExpect(status().isForbidden());
    }
}