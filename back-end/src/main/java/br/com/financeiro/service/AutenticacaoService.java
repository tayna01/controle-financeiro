package br.com.financeiro.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.financeiro.dto.AuthResponse;
import br.com.financeiro.dto.ChangePasswordRequest;
import br.com.financeiro.dto.ForgotPasswordRequest;
import br.com.financeiro.dto.ForgotPasswordResponse;
import br.com.financeiro.dto.LoginRequest;
import br.com.financeiro.dto.MessageResponse;
import br.com.financeiro.dto.RegisterRequest;
import br.com.financeiro.dto.ResetPasswordRequest;
import br.com.financeiro.dto.TransactionType;
import br.com.financeiro.dto.UpdateUserRequest;
import br.com.financeiro.dto.UserResponse;
import br.com.financeiro.entity.Categoria;
import br.com.financeiro.entity.TokenRedefinicaoSenha;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.BusinessException;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.exception.InvalidTokenException;
import br.com.financeiro.repository.CategoriaRepository;
import br.com.financeiro.repository.TokenRedefinicaoSenhaRepository;
import br.com.financeiro.repository.UsuarioRepository;
import br.com.financeiro.security.JwtService;

@Service
public class AutenticacaoService implements UserDetailsService {

    private static final String MENSAGEM_NEUTRA = "Se este e-mail estiver cadastrado, você receberá as instruções em breve.";

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TokenRedefinicaoSenhaRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new ConflictException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getName());
        usuario.setEmail(request.getEmail());
        usuario.setSenhaCriptografada(passwordEncoder.encode(request.getPassword()));
        usuario = usuarioRepository.save(usuario);

        criarCategoriasPadroes(usuario);
        return toUserResponse(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        String accessToken = jwtService.generateToken(request.getEmail());
        return new AuthResponse(accessToken, "Bearer", jwtService.getExpirationSeconds());
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String debugToken = null;
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.getEmail()).orElse(null);

        if (usuario != null) {
            List<TokenRedefinicaoSenha> tokensAntigos = tokenRepository.findByUsuarioIdAndUtilizadoFalse(usuario.getId());
            for (TokenRedefinicaoSenha antigo : tokensAntigos) {
                antigo.setUtilizado(true);
                tokenRepository.save(antigo);
            }

            TokenRedefinicaoSenha token = new TokenRedefinicaoSenha();
            token.setUsuario(usuario);
            token.setToken(UUID.randomUUID().toString().replace("-", ""));
            token.setExpiraEm(LocalDateTime.now().plusHours(1));
            token.setUtilizado(false);
            tokenRepository.save(token);

            debugToken = token.getToken();
        }
        return new ForgotPasswordResponse(MENSAGEM_NEUTRA, debugToken);
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        TokenRedefinicaoSenha registro = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Token inválido ou expirado"));

        if (registro.isUtilizado() || registro.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token inválido ou expirado");
        }

        Usuario usuario = registro.getUsuario();
        usuario.setSenhaCriptografada(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);

        registro.setUtilizado(true);
        tokenRepository.save(registro);

        return new MessageResponse("Senha redefinida com sucesso.");
    }

    public UserResponse getProfile(Usuario usuario) {
        return toUserResponse(usuario);
    }

    @Transactional
    public UserResponse updateProfile(Usuario usuario, UpdateUserRequest request) {
        usuario.setNome(request.getName());
        return toUserResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public MessageResponse changePassword(Usuario usuario, ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), usuario.getSenhaCriptografada())) {
            throw new BusinessException("Senha atual incorreta");
        }
        usuario.setSenhaCriptografada(passwordEncoder.encode(request.getNewPassword()));
        usuarioRepository.save(usuario);
        return new MessageResponse("Senha alterada com sucesso.");
    }

    private void criarCategoriasPadroes(Usuario usuario) {
        String[][] padroes = {
                { "Salário", "INCOME" },
                { "Investimentos", "INCOME" },
                { "Alimentação", "EXPENSE" },
                { "Transporte", "EXPENSE" },
                { "Moradia", "EXPENSE" },
                { "Lazer", "EXPENSE" },
                { "Saúde", "EXPENSE" }
        };
        for (String[] padrao : padroes) {
            Categoria categoria = new Categoria();
            categoria.setUsuario(usuario);
            categoria.setNome(padrao[0]);
            categoria.setTipo(TransactionType.valueOf(padrao[1]).toEntity());
            categoriaRepository.save(categoria);
        }
    }

    private UserResponse toUserResponse(Usuario usuario) {
        return new UserResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCriadoEm()
        );
    }
}
