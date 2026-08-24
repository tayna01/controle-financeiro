package br.com.financeiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.financeiro.dto.ChangePasswordRequest;
import br.com.financeiro.dto.MessageResponse;
import br.com.financeiro.dto.UpdateUserRequest;
import br.com.financeiro.dto.UserResponse;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.AutenticacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UsuarioController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GetMapping("/me")
    public UserResponse me() {
        return autenticacaoService.getProfile(currentUserProvider.getCurrentUser());
    }

    @PutMapping("/me")
    public UserResponse update(@Valid @RequestBody UpdateUserRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return autenticacaoService.updateProfile(usuario, request);
    }

    @PatchMapping("/me/password")
    public MessageResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return autenticacaoService.changePassword(usuario, request);
    }
}
