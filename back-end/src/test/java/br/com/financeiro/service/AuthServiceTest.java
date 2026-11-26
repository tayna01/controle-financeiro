package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.financeiro.dto.AuthResponse;
import br.com.financeiro.dto.LoginRequest;
import br.com.financeiro.dto.RegisterRequest;
import br.com.financeiro.entity.Category;
import br.com.financeiro.entity.User;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.repository.CategoryRepository;
import br.com.financeiro.repository.PasswordResetTokenRepository;
import br.com.financeiro.repository.UserRepository;
import br.com.financeiro.repository.WalletMemberRepository;
import br.com.financeiro.repository.WalletRepository;
import br.com.financeiro.security.JwtService;
import br.com.financeiro.service.EmailService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMemberRepository walletMemberRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesUserWithDefaultCategoriesAndTrimsInputs() {
        User saved = new User();
        saved.setId(1L);

        when(userRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
        when(passwordEncoder.encode("StrongPass@1")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(walletRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        authService.register(new RegisterRequest("  Ana Souza  ", "  ANA@Example.com ", "StrongPass@1"));

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("Ana Souza", captor.getValue().getName());
        assertEquals("ana@example.com", captor.getValue().getEmail());
        verify(categoryRepository, times(7)).save(any(Category.class));
    }

    @Test
    void registerWithDuplicateEmailThrowsConflict() {
        when(userRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authService
                .register(new RegisterRequest("Ana", "ana@example.com", "StrongPass@1")));

        verify(userRepository, never()).save(any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void loginWithInvalidCredentialsThrowsBadCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("invalid password"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("ana@example.com", "wrong")));
    }

    @Test
    void loginWithSuccessReturnsToken() {
        User user = new User();
        user.setId(1L);
        user.setName("Ana");
        user.setEmail("ana@example.com");

        when(authenticationManager.authenticate(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.findByEmailIgnoreCase("ana@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("test-token");
        when(jwtService.getExpirationSeconds()).thenReturn(86400L);

        AuthResponse response = authService.login(new LoginRequest("ana@example.com", "StrongPass@1"));

        assertEquals("test-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(86400L, response.getExpiresIn());
    }
}