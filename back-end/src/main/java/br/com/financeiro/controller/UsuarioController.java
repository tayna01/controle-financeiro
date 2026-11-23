package br.com.financeiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserResponse> me() {
        return ResponseEntity.ok(autenticacaoService.getProfile(currentUserProvider.getCurrentUser()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> update(@Valid @RequestBody UpdateUserRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(autenticacaoService.updateProfile(usuario, request));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(autenticacaoService.changePassword(usuario, request));
    }
}