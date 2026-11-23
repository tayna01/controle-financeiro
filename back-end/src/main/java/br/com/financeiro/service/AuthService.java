package br.com.financeiro.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import br.com.financeiro.dto.ForgotPasswordRequest;
import br.com.financeiro.dto.ForgotPasswordResponse;
import br.com.financeiro.dto.LoginRequest;
import br.com.financeiro.dto.MessageResponse;
import br.com.financeiro.dto.RegisterRequest;
import br.com.financeiro.dto.ResetPasswordRequest;
import br.com.financeiro.dto.UserResponse;
import br.com.financeiro.entity.Category;
import br.com.financeiro.entity.PasswordResetToken;
import br.com.financeiro.entity.TransactionType;
import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.entity.WalletMember;
import br.com.financeiro.entity.WalletRole;
import br.com.financeiro.exception.BusinessException;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.exception.InvalidTokenException;
import br.com.financeiro.repository.CategoryRepository;
import br.com.financeiro.repository.PasswordResetTokenRepository;
import br.com.financeiro.repository.UserRepository;
import br.com.financeiro.repository.WalletMemberRepository;
import br.com.financeiro.repository.WalletRepository;
import br.com.financeiro.security.JwtService;
import br.com.financeiro.util.TextUtil;

@Service
public class AuthService implements UserDetailsService {

    private static final String NEUTRAL_MESSAGE = "Se este e-mail estiver cadastrado, você receberá as instruções em breve.";

    @Value("${app.debug-tokens:false}")
    private boolean debugTokensEnabled;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMemberRepository walletMemberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String email = TextUtil.trimToNull(request.getEmail()).toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("E-mail já cadastrado");
        }

        User user = new User();
        user.setName(TextUtil.trimToNull(request.getName()));
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user = userRepository.save(user);

        createDefaultCategories(user);
        createDefaultWallet(user);
        return toUserResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        String accessToken = jwtService.generateToken(user);
        return new AuthResponse(accessToken, "Bearer", jwtService.getExpirationSeconds());
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String debugToken = null;
        User user = userRepository.findByEmailIgnoreCase(request.getEmail()).orElse(null);

        if (user != null) {
            List<PasswordResetToken> oldTokens = tokenRepository.findByUserIdAndUsedFalse(user.getId());
            for (PasswordResetToken old : oldTokens) {
                old.setUsed(true);
                tokenRepository.save(old);
            }

            PasswordResetToken token = new PasswordResetToken();
            token.setUser(user);
            token.setToken(UUID.randomUUID().toString().replace("-", ""));
            token.setExpiresAt(LocalDateTime.now().plusHours(1));
            token.setUsed(false);
            tokenRepository.save(token);

            if (debugTokensEnabled) {
                debugToken = token.getToken();
            }
        }
        return new ForgotPasswordResponse(NEUTRAL_MESSAGE, debugToken);
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken record = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Token inválido ou expirado"));

        if (record.isUsed() || record.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token inválido ou expirado");
        }

        User user = record.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        record.setUsed(true);
        tokenRepository.save(record);

        return new MessageResponse("Senha redefinida com sucesso.");
    }

    public UserResponse getProfile(User user) {
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(User user, br.com.financeiro.dto.UpdateUserRequest request) {
        user.setName(TextUtil.trimToNull(request.getName()));
        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public MessageResponse changePassword(User user, br.com.financeiro.dto.ChangePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessException("Senha atual incorreta");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new MessageResponse("Senha alterada com sucesso.");
    }

    private void createDefaultCategories(User user) {
        String[][] defaults = {
                { "Salário", "INCOME" },
                { "Investimentos", "INCOME" },
                { "Alimentação", "EXPENSE" },
                { "Transporte", "EXPENSE" },
                { "Moradia", "EXPENSE" },
                { "Lazer", "EXPENSE" },
                { "Saúde", "EXPENSE" }
        };
        for (String[] item : defaults) {
            Category category = new Category();
            category.setUser(user);
            category.setName(item[0]);
            category.setType(TransactionType.valueOf(item[1]));
            categoryRepository.save(category);
        }
    }

    private void createDefaultWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setOwner(user);
        wallet.setName("Carteira Pessoal");
        wallet = walletRepository.save(wallet);

        WalletMember member = new WalletMember();
        member.setWallet(wallet);
        member.setUser(user);
        member.setRole(WalletRole.OWNER);
        walletMemberRepository.save(member);
    }

    private UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt());
    }
}