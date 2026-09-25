package br.com.financeiro.security;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.financeiro.exception.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Limita tentativas em rotas sensíveis (login, recuperação e cadastro).
 * Janela fixa por cliente (IP), configurável via properties.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    @Value("${security.rate-limit.auth.max-requests:5}")
    private int maxRequests;

    @Value("${security.rate-limit.auth.window-seconds:60}")
    private long windowSeconds;

    @Autowired
    private ObjectMapper objectMapper;

    private final Map<String, Janela> janelas = new ConcurrentHashMap<>();

    private static class Janela {
        long inicio;
        int contador;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getMethod().equalsIgnoreCase("POST")
                || !request.getRequestURI().startsWith("/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long janeleMillis = Math.max(1, windowSeconds) * 1000L;
        long agora = System.currentTimeMillis();
        String chave = clientIp(request);

        if (janelas.size() > 10_000) {
            janelas.entrySet().removeIf(entrada -> agora - entrada.getValue().inicio > janeleMillis);
        }

        Janela janela = janelas.computeIfAbsent(chave, k -> novaJanela(agora));
        synchronized (janela) {
            if (agora - janela.inicio >= janeleMillis) {
                janela.inicio = agora;
                janela.contador = 0;
            }
            if (janela.contador >= maxRequests) {
                recusar(response);
                return;
            }
            janela.contador++;
        }

        chain.doFilter(request, response);
    }

    private Janela novaJanela(long agora) {
        Janela janela = new Janela();
        janela.inicio = agora;
        return janela;
    }

    private String clientIp(HttpServletRequest request) {
        String forward = request.getHeader("X-Forwarded-For");
        if (forward != null && !forward.isBlank()) {
            return forward.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void recusar(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(),
                ApiError.of(429, "Too Many Requests",
                        "Muitas tentativas. Tente novamente em instantes."));
    }
}