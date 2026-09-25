package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import br.com.financeiro.dto.TransactionRequest;
import br.com.financeiro.dto.TransactionType;
import br.com.financeiro.entity.Carteira;
import br.com.financeiro.entity.CarteiraMembro;
import br.com.financeiro.entity.PapelCarteira;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CarteiraMembroRepository;
import br.com.financeiro.repository.CarteiraRepository;
import br.com.financeiro.repository.CategoriaRepository;
import br.com.financeiro.repository.TransacaoRepository;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private CarteiraMembroRepository membroRepository;

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private TransacaoService transacaoService;

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

    private TransactionRequest request(BigDecimal valor) {
        return new TransactionRequest(TransactionType.EXPENSE, valor, "Teste",
                LocalDate.now(), null);
    }

    @Test
    void visualizadorNaoPodeCriarTransacao() {
        Usuario dono = usuario(1L);
        Usuario visualizador = usuario(2L);
        Carteira carteira = carteira(dono, 10L);

        CarteiraMembro membro = new CarteiraMembro();
        membro.setPapel(PapelCarteira.VISUALIZADOR);

        when(carteiraRepository.findById(10L)).thenReturn(Optional.of(carteira));
        when(membroRepository.existsByCarteiraIdAndUsuarioId(10L, 2L)).thenReturn(true);
        when(membroRepository.findByCarteiraIdAndUsuarioId(10L, 2L)).thenReturn(Optional.of(membro));

        assertThrows(AccessDeniedException.class,
                () -> transacaoService.create(visualizador, 10L, request(new BigDecimal("50"))));

        verify(transacaoRepository, never()).save(any());
    }

    @Test
    void outsiderNaoConsegueListarTransacoes() {
        Usuario dono = usuario(1L);
        Usuario outsider = usuario(99L);
        Carteira carteira = carteira(dono, 10L);

        when(carteiraRepository.findById(10L)).thenReturn(Optional.of(carteira));
        when(membroRepository.existsByCarteiraIdAndUsuarioId(10L, 99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> transacaoService
                .list(outsider, 10L, null, null, null, null, Pageable.unpaged()));
    }

    @Test
    void editorConsegueCriarTransacao() {
        Usuario dono = usuario(1L);
        Usuario editor = usuario(2L);
        Carteira carteira = carteira(dono, 10L);

        CarteiraMembro membro = new CarteiraMembro();
        membro.setPapel(PapelCarteira.EDITOR);

        when(carteiraRepository.findById(10L)).thenReturn(Optional.of(carteira));
        when(membroRepository.existsByCarteiraIdAndUsuarioId(10L, 2L)).thenReturn(true);
        when(membroRepository.findByCarteiraIdAndUsuarioId(10L, 2L)).thenReturn(Optional.of(membro));
        when(transacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        transacaoService.create(editor, 10L, request(new BigDecimal("50")));

        verify(transacaoRepository).save(any());
    }
}