package br.com.financeiro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.financeiro.dto.MemberRequest;
import br.com.financeiro.dto.MemberResponse;
import br.com.financeiro.dto.MemberRoleRequest;
import br.com.financeiro.dto.WalletRequest;
import br.com.financeiro.dto.WalletResponse;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.CarteiraService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wallets")
public class CarteiraController {

    @Autowired
    private CarteiraService carteiraService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<WalletResponse>> list() {
        return ResponseEntity.ok(carteiraService.list(currentUser()));
    }

    @PostMapping
    public ResponseEntity<WalletResponse> create(@Valid @RequestBody WalletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carteiraService.create(currentUser(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(carteiraService.get(currentUser(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletResponse> update(@PathVariable Long id, @Valid @RequestBody WalletRequest request) {
        return ResponseEntity.ok(carteiraService.update(currentUser(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carteiraService.delete(currentUser(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<MemberResponse>> listMembers(@PathVariable Long id) {
        return ResponseEntity.ok(carteiraService.listMembers(currentUser(), id));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<MemberResponse> addMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carteiraService.addMember(currentUser(), id, request));
    }

    @PatchMapping("/{id}/members/{userId}")
    public ResponseEntity<MemberResponse> updateRole(@PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody MemberRoleRequest request) {
        return ResponseEntity.ok(carteiraService.updateRole(currentUser(), id, userId, request));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        carteiraService.removeMember(currentUser(), id, userId);
        return ResponseEntity.noContent().build();
    }

    private Usuario currentUser() {
        return currentUserProvider.getCurrentUser();
    }
}