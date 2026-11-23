package br.com.financeiro.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.financeiro.dto.SummaryResponse;
import br.com.financeiro.dto.TransactionRequest;
import br.com.financeiro.dto.TransactionResponse;
import br.com.financeiro.entity.TransactionType;
import br.com.financeiro.entity.User;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.SummaryService;
import br.com.financeiro.service.TransactionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wallets/{walletId}")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionResponse>> list(@PathVariable Long walletId,
            @RequestParam(value = "type", required = false) TransactionType type,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            Pageable pageable) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(transactionService.list(user, walletId, type, categoryId, startDate, endDate, pageable));
    }

    @PostMapping("/transactions")
    public ResponseEntity<TransactionResponse> create(@PathVariable Long walletId,
            @Valid @RequestBody TransactionRequest request) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(user, walletId, request));
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> get(@PathVariable Long walletId, @PathVariable Long id) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(transactionService.get(user, walletId, id));
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long walletId,
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(transactionService.update(user, walletId, id, request));
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long walletId, @PathVariable Long id) {
        User user = currentUserProvider.getCurrentUser();
        transactionService.delete(user, walletId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> summary(@PathVariable Long walletId,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(summaryService.summary(user, walletId, startDate, endDate));
    }
}