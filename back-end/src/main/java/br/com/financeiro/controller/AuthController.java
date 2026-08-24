package br.com.financeiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.financeiro.dto.AuthResponse;
import br.com.financeiro.dto.ForgotPasswordRequest;
import br.com.financeiro.dto.ForgotPasswordResponse;
import br.com.financeiro.dto.LoginRequest;
import br.com.financeiro.dto.MessageResponse;
import br.com.financeiro.dto.RegisterRequest;
import br.com.financeiro.dto.ResetPasswordRequest;
import br.com.financeiro.dto.UserResponse;
import br.com.financeiro.service.AutenticacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return autenticacaoService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return autenticacaoService.login(request);
    }

    @PostMapping("/forgot-password")
    public ForgotPasswordResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return autenticacaoService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public MessageResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return autenticacaoService.resetPassword(request);
    }
}
