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
import org.springframework.security.access.AccessDeniedException;

import br.com.financeiro.dto.SummaryResponse;
import br.com.financeiro.entity.Carteira;
import br.com.financeiro.entity.Categoria;
import br.com.financeiro.entity.TipoTransacao;
import br.com.financeiro.entity.Transacao;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.TransacaoRepository;

@ExtendWith(MockitoExtension.class)
class ResumoFinanceiroServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private AcessoCarteiraService acessoCarteiraService;

    @InjectMocks
    private ResumoFinanceiroService resumoFinanceiroService;

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Usuario " + id);
        usuario.setEmail("user" + id + "@exemplo.com");
        return usuario;
    }

    private Carteira carteira(Usuario dono, Long id) {
        Carteira carteira = new Carteira();
        carteira.setId(id);
        carteira.setDono(dono);
        carteira.setNome("Carteira");
        return carteira;
    }

    private Transacao transacao(TipoTransacao tipo, BigDecimal valor, LocalDate data, Categoria categoria) {
        Transacao transacao = new Transacao();
        transacao.setTipo(tipo);
        transacao.setValor(valor);
        transacao.setData(data);
        transacao.setCategoria(categoria);
        return transacao;
    }

    private Categoria categoria(Long id, String nome) {
        Categoria categoria = new Categoria();
        categoria.setId(id);
        categoria.setNome(nome);
        return categoria;
    }

    @Test
    void totaisReceitaDespesaESaldo() {
        Usuario dono = usuario(1L);
        Carteira carteira = carteira(dono, 10L);
        LocalDate inicio = LocalDate.of(2026, 9, 1);
        LocalDate fim = LocalDate.of(2026, 9, 30);

        List<Transacao> transacoes = List.of(
                transacao(TipoTransacao.RECEITA, new BigDecimal("100"), LocalDate.of(2026, 9, 5), null),
                transacao(TipoTransacao.DESPESA, new BigDecimal("40"), LocalDate.of(2026, 9, 10), null));

        Page<Transacao> pagina = new PageImpl<>(transacoes, Pageable.unpaged(), transacoes.size());

        when(acessoCarteiraService.buscarCarteiraComAcesso(dono, 10L)).thenReturn(carteira);
        when(transacaoRepository.listarComFiltros(10L, null, null, inicio, fim, Pageable.unpaged()))
                .thenReturn(pagina);

        SummaryResponse resposta = resumoFinanceiroService.summary(dono, 10L, inicio, fim);

        assertEquals(new BigDecimal("100"), resposta.getTotalIncome());
        assertEquals(new BigDecimal("40"), resposta.getTotalExpense());
        assertEquals(new BigDecimal("60"), resposta.getBalance());
        assertEquals(2, resposta.getTransactionCount());
    }

    @Test
    void agrupamentoPorCategoria() {
        Usuario dono = usuario(1L);
        Carteira carteira = carteira(dono, 10L);
        LocalDate inicio = LocalDate.of(2026, 9, 1);
        LocalDate fim = LocalDate.of(2026, 9, 30);

        Categoria alimentacao = categoria(1L, "Alimentação");
        Categoria transporte = categoria(2L, "Transporte");

        List<Transacao> transacoes = List.of(
                transacao(TipoTransacao.DESPESA, new BigDecimal("30"), LocalDate.of(2026, 9, 5), alimentacao),
                transacao(TipoTransacao.DESPESA, new BigDecimal("20"), LocalDate.of(2026, 9, 6), alimentacao),
                transacao(TipoTransacao.DESPESA, new BigDecimal("15"), LocalDate.of(2026, 9, 7), transporte));

        Page<Transacao> pagina = new PageImpl<>(transacoes, Pageable.unpaged(), transacoes.size());

        when(acessoCarteiraService.buscarCarteiraComAcesso(dono, 10L)).thenReturn(carteira);
        when(transacaoRepository.listarComFiltros(10L, null, null, inicio, fim, Pageable.unpaged()))
                .thenReturn(pagina);

        SummaryResponse resposta = resumoFinanceiroService.summary(dono, 10L, inicio, fim);

        assertEquals(2, resposta.getByCategory().size());
    }

    @Test
    void agrupamentoPorMes() {
        Usuario dono = usuario(1L);
        Carteira carteira = carteira(dono, 10L);
        LocalDate inicio = LocalDate.of(2026, 8, 1);
        LocalDate fim = LocalDate.of(2026, 9, 30);

        List<Transacao> transacoes = List.of(
                transacao(TipoTransacao.RECEITA, new BigDecimal("100"), LocalDate.of(2026, 8, 5), null),
                transacao(TipoTransacao.DESPESA, new BigDecimal("40"), LocalDate.of(2026, 9, 10), null));

        Page<Transacao> pagina = new PageImpl<>(transacoes, Pageable.unpaged(), transacoes.size());

        when(acessoCarteiraService.buscarCarteiraComAcesso(dono, 10L)).thenReturn(carteira);
        when(transacaoRepository.listarComFiltros(10L, null, null, inicio, fim, Pageable.unpaged()))
                .thenReturn(pagina);

        SummaryResponse resposta = resumoFinanceiroService.summary(dono, 10L, inicio, fim);

        assertEquals(2, resposta.getByMonth().size());
    }

    @Test
    void transacaoSemCategoria() {
        Usuario dono = usuario(1L);
        Carteira carteira = carteira(dono, 10L);
        LocalDate inicio = LocalDate.of(2026, 9, 1);
        LocalDate fim = LocalDate.of(2026, 9, 30);

        List<Transacao> transacoes = List.of(
                transacao(TipoTransacao.DESPESA, new BigDecimal("25"), LocalDate.of(2026, 9, 5), null));

        Page<Transacao> pagina = new PageImpl<>(transacoes, Pageable.unpaged(), transacoes.size());

        when(acessoCarteiraService.buscarCarteiraComAcesso(dono, 10L)).thenReturn(carteira);
        when(transacaoRepository.listarComFiltros(10L, null, null, inicio, fim, Pageable.unpaged()))
                .thenReturn(pagina);

        SummaryResponse resposta = resumoFinanceiroService.summary(dono, 10L, inicio, fim);

        assertEquals(0, resposta.getByCategory().size());
        assertEquals(1, resposta.getTransactionCount());
    }

    @Test
    void periodoSemTransacoesTudoZerado() {
        Usuario dono = usuario(1L);
        Carteira carteira = carteira(dono, 10L);
        LocalDate inicio = LocalDate.of(2026, 9, 1);
        LocalDate fim = LocalDate.of(2026, 9, 30);

        Page<Transacao> pagina = new PageImpl<>(List.of(), Pageable.unpaged(), 0);

        when(acessoCarteiraService.buscarCarteiraComAcesso(dono, 10L)).thenReturn(carteira);
        when(transacaoRepository.listarComFiltros(10L, null, null, inicio, fim, Pageable.unpaged()))
                .thenReturn(pagina);

        SummaryResponse resposta = resumoFinanceiroService.summary(dono, 10L, inicio, fim);

        assertEquals(BigDecimal.ZERO, resposta.getTotalIncome());
        assertEquals(BigDecimal.ZERO, resposta.getTotalExpense());
        assertEquals(BigDecimal.ZERO, resposta.getBalance());
        assertEquals(0, resposta.getTransactionCount());
        assertEquals(0, resposta.getByCategory().size());
        assertEquals(0, resposta.getByMonth().size());
    }

    @Test
    void acessoNegado() {
        Usuario outsider = usuario(99L);

        when(acessoCarteiraService.buscarCarteiraComAcesso(outsider, 10L))
                .thenThrow(new ResourceNotFoundException("Carteira não encontrada"));

        assertThrows(ResourceNotFoundException.class,
                () -> resumoFinanceiroService.summary(outsider, 10L, null, null));
    }
}
