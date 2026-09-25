package br.com.financeiro.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.FilterChain;

class RateLimitFilterTest {

    private RateLimitFilter filter;

    @BeforeEach
    void setUp() throws Exception {
        filter = new RateLimitFilter();
        ReflectionTestUtils.setField(filter, "maxRequests", 2);
        ReflectionTestUtils.setField(filter, "windowSeconds", 60L);
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ReflectionTestUtils.setField(filter, "objectMapper", mapper);
    }

    @Test
    void permiteRequisicoesDentroDoLimite() throws Exception {
        AtomicInteger chamadas = new AtomicInteger();
        FilterChain chain = (req, res) -> chamadas.incrementAndGet();

        filter.doFilter(requisicao("POST", "/auth/login"), new MockHttpServletResponse(), chain);
        filter.doFilter(requisicao("POST", "/auth/login"), new MockHttpServletResponse(), chain);

        assertEquals(2, chamadas.get());
    }

    @Test
    void bloqueiaComTooManyRequestsQuandoExcedeLimite() throws Exception {
        AtomicInteger chamadas = new AtomicInteger();
        FilterChain chain = (req, res) -> chamadas.incrementAndGet();

        filter.doFilter(requisicao("POST", "/auth/login"), new MockHttpServletResponse(), chain);
        filter.doFilter(requisicao("POST", "/auth/login"), new MockHttpServletResponse(), chain);

        MockHttpServletResponse bloqueada = new MockHttpServletResponse();
        filter.doFilter(requisicao("POST", "/auth/login"), bloqueada, chain);

        assertEquals(429, bloqueada.getStatus());
        assertTrue(bloqueada.getContentType().startsWith("application/json"));
        assertTrue(bloqueada.getContentAsString().contains("Muitas tentativas"));
        assertEquals(2, chamadas.get());
    }

    @Test
    void naoLimitaMetodoGet() throws Exception {
        AtomicInteger chamadas = new AtomicInteger();
        FilterChain chain = (req, res) -> chamadas.incrementAndGet();

        filter.doFilter(requisicao("GET", "/auth/login"), new MockHttpServletResponse(), chain);

        assertEquals(1, chamadas.get());
    }

    @Test
    void naoLimitaOutrasRotas() throws Exception {
        AtomicInteger chamadas = new AtomicInteger();
        FilterChain chain = (req, res) -> chamadas.incrementAndGet();

        filter.doFilter(requisicao("POST", "/api/v1/wallets"), new MockHttpServletResponse(), chain);

        assertEquals(1, chamadas.get());
    }

    private MockHttpServletRequest requisicao(String metodo, String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest(metodo, uri);
        request.setRemoteAddr("127.0.0.1");
        return request;
    }
}