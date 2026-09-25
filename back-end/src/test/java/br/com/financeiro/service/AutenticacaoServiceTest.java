package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.financeiro.dto.AuthResponse;
import br.com.financeiro.dto.LoginRequest;
import br.com.financeiro.dto.RegisterRequest;
import br.com.financeiro.entity.Categoria;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.repository.CategoriaRepository;
import br.com.financeiro.repository.TokenRedefinicaoSenhaRepository;
import br.com.financeiro.repository.UsuarioRepository;
import br.com.financeiro.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private TokenRedefinicaoSenhaRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Test
    void registerCriaUsuarioComCategoriasPadroesETrimandoEntradas() {
        Usuario salvo = new Usuario();
        salvo.setId(1L);

        when(usuarioRepository.existsByEmailIgnoreCase("ana@exemplo.com")).thenReturn(false);
        when(passwordEncoder.encode("SenhaForte@1")).thenReturn("hash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(salvo);

        autenticacaoService.register(new RegisterRequest("  Ana Souza  ", "  ANA@Exemplo.com ", "SenhaForte@1"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertEquals("Ana Souza", captor.getValue().getNome());
        assertEquals("ana@exemplo.com", captor.getValue().getEmail());
        verify(categoriaRepository, times(7)).save(any(Categoria.class));
    }

    @Test
    void registerComEmailDuplicadoLancaConflito() {
        when(usuarioRepository.existsByEmailIgnoreCase("ana@exemplo.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> autenticacaoService
                .register(new RegisterRequest("Ana", "ana@exemplo.com", "SenhaForte@1")));

        verify(usuarioRepository, never()).save(any());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void loginComCredenciaisInvalidasLancaBadCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("senha inválida"));

        assertThrows(BadCredentialsException.class,
                () -> autenticacaoService.login(new LoginRequest("ana@exemplo.com", "errada")));
    }

    @Test
    void loginComSucessoRetornaToken() {
        when(authenticationManager.authenticate(any())).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken("ana@exemplo.com")).thenReturn("token-de-teste");
        when(jwtService.getExpirationSeconds()).thenReturn(86400L);

        AuthResponse resposta = autenticacaoService.login(new LoginRequest("ana@exemplo.com", "SenhaForte@1"));

        assertEquals("token-de-teste", resposta.getAccessToken());
        assertEquals("Bearer", resposta.getTokenType());
        assertEquals(86400L, resposta.getExpiresIn());
    }
}