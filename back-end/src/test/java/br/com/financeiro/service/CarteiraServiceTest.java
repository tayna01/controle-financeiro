package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.financeiro.dto.MemberRequest;
import br.com.financeiro.entity.Carteira;
import br.com.financeiro.entity.PapelCarteira;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CarteiraMembroRepository;
import br.com.financeiro.repository.CarteiraRepository;
import br.com.financeiro.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class CarteiraServiceTest {

    @Mock
    private CarteiraRepository carteiraRepository;

    @Mock
    private CarteiraMembroRepository membroRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CarteiraService carteiraService;

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Usuario " + id);
        usuario.setEmail("user" + id + "@exemplo.com");
        return usuario;
    }

    @Test
    void adicionarMembroInexistenteLancaNaoEncontrado() {
        Usuario dono = usuario(1L);
        Carteira carteira = new Carteira();
        carteira.setId(10L);
        carteira.setDono(dono);

        when(carteiraRepository.findById(10L)).thenReturn(Optional.of(carteira));
        when(membroRepository.existsByCarteiraIdAndUsuarioId(10L, 1L)).thenReturn(true);
        when(usuarioRepository.findByEmailIgnoreCase("quem@exemplo.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carteiraService
                .addMember(dono, 10L, new MemberRequest("quem@exemplo.com", PapelCarteira.EDITOR)));
    }

    @Test
    void adicionarMembroJaExistenteLancaConflito() {
        Usuario dono = usuario(1L);
        Usuario convidado = usuario(2L);
        Carteira carteira = new Carteira();
        carteira.setId(10L);
        carteira.setDono(dono);

        when(carteiraRepository.findById(10L)).thenReturn(Optional.of(carteira));
        when(membroRepository.existsByCarteiraIdAndUsuarioId(10L, 1L)).thenReturn(true);
        when(usuarioRepository.findByEmailIgnoreCase("convidado@exemplo.com")).thenReturn(Optional.of(convidado));
        when(membroRepository.existsByCarteiraIdAndUsuarioId(10L, 2L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> carteiraService
                .addMember(dono, 10L, new MemberRequest("convidado@exemplo.com", PapelCarteira.EDITOR)));
    }

    @Test
    void acessarCarteiraDeOutroUsuarioRetornaNaoEncontrado() {
        Usuario dono = usuario(1L);
        Usuario outsider = usuario(9L);
        Carteira carteira = new Carteira();
        carteira.setId(10L);
        carteira.setDono(dono);

        when(carteiraRepository.findById(10L)).thenReturn(Optional.of(carteira));
        when(membroRepository.existsByCarteiraIdAndUsuarioId(10L, 9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> carteiraService.get(outsider, 10L));
    }
}