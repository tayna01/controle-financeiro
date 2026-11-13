package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import br.com.financeiro.entity.Carteira;
import br.com.financeiro.entity.CarteiraMembro;
import br.com.financeiro.entity.PapelCarteira;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CarteiraMembroRepository;
import br.com.financeiro.repository.CarteiraRepository;

@ExtendWith(MockitoExtension.class)
class AcessoCarteiraServiceTest {

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private CarteiraMembroRepository membroRepository;

    @InjectMocks
    private AcessoCarteiraService acessoCarteiraService;

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

    @Test
    void semAcessoLancaNaoEncontrado() {
        Usuario dono = usuario(1L);
        Usuario outsider = usuario(99L);
        Carteira carteira = carteira(dono, 10L);

        org.mockito.Mockito.when(carteiraRepository.findById(10L)).thenReturn(Optional.of(carteira));
        org.mockito.Mockito.when(membroRepository.existsByCarteiraIdAndUsuarioId(10L, 99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> acessoCarteiraService.buscarCarteiraComAcesso(outsider, 10L));
    }

    @Test
    void visualizadorNaoTemPermissaoEscrita() {
        Usuario dono = usuario(1L);
        Usuario visualizador = usuario(2L);
        Carteira carteira = carteira(dono, 10L);

        CarteiraMembro membro = new CarteiraMembro();
        membro.setPapel(PapelCarteira.VISUALIZADOR);

        org.mockito.Mockito.when(membroRepository.findByCarteiraIdAndUsuarioId(10L, 2L))
                .thenReturn(Optional.of(membro));

        assertEquals(PapelCarteira.VISUALIZADOR, acessoCarteiraService.papelDoUsuario(visualizador, carteira));
        assertThrows(AccessDeniedException.class,
                () -> acessoCarteiraService.exigirPermissaoEscrita(visualizador, carteira));
    }

    @Test
    void editorTemPermissaoEscrita() {
        Usuario dono = usuario(1L);
        Usuario editor = usuario(2L);
        Carteira carteira = carteira(dono, 10L);

        CarteiraMembro membro = new CarteiraMembro();
        membro.setPapel(PapelCarteira.EDITOR);

        org.mockito.Mockito.when(membroRepository.findByCarteiraIdAndUsuarioId(10L, 2L))
                .thenReturn(Optional.of(membro));

        assertEquals(PapelCarteira.EDITOR, acessoCarteiraService.papelDoUsuario(editor, carteira));
        assertDoesNotThrow(() -> acessoCarteiraService.exigirPermissaoEscrita(editor, carteira));
    }

    @Test
    void donoTemAcessoEPermissao() {
        Usuario dono = usuario(1L);
        Carteira carteira = carteira(dono, 10L);

        assertEquals(PapelCarteira.DONO, acessoCarteiraService.papelDoUsuario(dono, carteira));
        assertDoesNotThrow(() -> acessoCarteiraService.exigirDono(dono, carteira));
        assertDoesNotThrow(() -> acessoCarteiraService.exigirPermissaoEscrita(dono, carteira));
    }
}
