package br.com.financeiro.security;

import br.com.financeiro.entity.Usuario;
import br.com.financeiro.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Sem JWT (Fase 5), todos os CRUDs operam com um usuário de desenvolvimento fixo.
 * Quando a autenticação for implementada, este provedor deve ler o SecurityContext.
 */
@Component
public class CurrentUserProvider {

    private static final String DEV_EMAIL = "dev@financeiro.local";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public CurrentUserProvider(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario getCurrentUser() {
        return usuarioRepository.findByEmailIgnoreCase(DEV_EMAIL)
                .orElseGet(this::criarUsuarioDev);
    }

    private Usuario criarUsuarioDev() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de Desenvolvimento");
        usuario.setEmail(DEV_EMAIL);
        usuario.setSenhaCriptografada(passwordEncoder.encode(UUID.randomUUID().toString()));
        return usuarioRepository.save(usuario);
    }
}