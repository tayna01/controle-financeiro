package br.com.financeiro.controller;

import br.com.financeiro.dto.MemberRequest;
import br.com.financeiro.dto.MemberResponse;
import br.com.financeiro.dto.MemberRoleRequest;
import br.com.financeiro.dto.WalletRequest;
import br.com.financeiro.dto.WalletResponse;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.CarteiraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
public class CarteiraController {

    @Autowired
    private CarteiraService carteiraService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GetMapping
    public List<WalletResponse> list() {
        return carteiraService.list(currentUser());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse create(@Valid @RequestBody WalletRequest request) {
        return carteiraService.create(currentUser(), request);
    }

    @GetMapping("/{id}")
    public WalletResponse get(@PathVariable Long id) {
        return carteiraService.get(currentUser(), id);
    }

    @PutMapping("/{id}")
    public WalletResponse update(@PathVariable Long id, @Valid @RequestBody WalletRequest request) {
        return carteiraService.update(currentUser(), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        carteiraService.delete(currentUser(), id);
    }

    @GetMapping("/{id}/members")
    public List<MemberResponse> listMembers(@PathVariable Long id) {
        return carteiraService.listMembers(currentUser(), id);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse addMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return carteiraService.addMember(currentUser(), id, request);
    }

    @PatchMapping("/{id}/members/{userId}")
    public MemberResponse updateRole(@PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody MemberRoleRequest request) {
        return carteiraService.updateRole(currentUser(), id, userId, request);
    }

    @DeleteMapping("/{id}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable Long id, @PathVariable Long userId) {
        carteiraService.removeMember(currentUser(), id, userId);
    }

    private Usuario currentUser() {
        return currentUserProvider.getCurrentUser();
    }
}