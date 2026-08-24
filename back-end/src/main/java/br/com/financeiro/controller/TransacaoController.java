package br.com.financeiro.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.financeiro.dto.SummaryResponse;
import br.com.financeiro.dto.TransactionRequest;
import br.com.financeiro.dto.TransactionResponse;
import br.com.financeiro.dto.TransactionType;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.TransacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wallets/{walletId}")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GetMapping("/transactions")
    public Page<TransactionResponse> list(@PathVariable Long walletId,
                                          @RequestParam(value = "type", required = false) TransactionType type,
                                          @RequestParam(value = "categoryId", required = false) Long categoryId,
                                          @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                          @RequestParam(value = "endDate", required = false) LocalDate endDate,
                                          Pageable pageable) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return transacaoService.list(usuario, walletId, type, categoryId, startDate, endDate, pageable);
    }

    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@PathVariable Long walletId,
                                      @Valid @RequestBody TransactionRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return transacaoService.create(usuario, walletId, request);
    }

    @GetMapping("/transactions/{id}")
    public TransactionResponse get(@PathVariable Long walletId, @PathVariable Long id) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return transacaoService.get(usuario, walletId, id);
    }

    @PutMapping("/transactions/{id}")
    public TransactionResponse update(@PathVariable Long walletId,
                                      @PathVariable Long id,
                                      @Valid @RequestBody TransactionRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return transacaoService.update(usuario, walletId, id, request);
    }

    @DeleteMapping("/transactions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long walletId, @PathVariable Long id) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        transacaoService.delete(usuario, walletId, id);
    }

    @GetMapping("/summary")
    public SummaryResponse summary(@PathVariable Long walletId,
                                   @RequestParam(value = "startDate", required = false) LocalDate startDate,
                                   @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return transacaoService.summary(usuario, walletId, startDate, endDate);
    }
}
