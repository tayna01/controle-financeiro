package br.com.financeiro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.UsuarioRepository;

@Component
public class CurrentUserProvider {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return usuarioRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado"));
    }
}
