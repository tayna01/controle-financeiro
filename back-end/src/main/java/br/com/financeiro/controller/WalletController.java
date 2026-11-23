package br.com.financeiro.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import br.com.financeiro.dto.MemberRequest;
import br.com.financeiro.dto.MemberResponse;
import br.com.financeiro.dto.MemberRoleRequest;
import br.com.financeiro.dto.WalletRequest;
import br.com.financeiro.dto.WalletResponse;
import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.WalletAccessService;
import br.com.financeiro.service.WalletEventPublisher;
import br.com.financeiro.repository.TransactionRepository;
import br.com.financeiro.report.CsvReportGenerator;
import br.com.financeiro.report.PdfReportGenerator;
import br.com.financeiro.report.ReportGenerator;
import br.com.financeiro.service.AiService;
import br.com.financeiro.service.WalletService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletEventPublisher walletEventPublisher;

    @Autowired
    private AiService aiService;

    @Autowired
    private WalletAccessService walletAccessService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<WalletResponse>> list() {
        return ResponseEntity.ok(walletService.list(currentUser()));
    }

    @PostMapping
    public ResponseEntity<WalletResponse> create(@Valid @RequestBody WalletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.create(currentUser(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WalletResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.get(currentUser(), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WalletResponse> update(@PathVariable Long id, @Valid @RequestBody WalletRequest request) {
        return ResponseEntity.ok(walletService.update(currentUser(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        walletService.delete(currentUser(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<MemberResponse>> listMembers(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.listMembers(currentUser(), id));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<MemberResponse> addMember(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.addMember(currentUser(), id, request));
    }

    @PatchMapping("/{id}/members/{userId}")
    public ResponseEntity<MemberResponse> updateRole(@PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody MemberRoleRequest request) {
        return ResponseEntity.ok(walletService.updateRole(currentUser(), id, userId, request));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long id, @PathVariable Long userId) {
        walletService.removeMember(currentUser(), id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(@PathVariable Long id) {
        User user = currentUserProvider.getCurrentUser();
        walletAccessService.findWalletWithAccess(user, id);
        return walletEventPublisher.subscribe(id);
    }

    @GetMapping("/reports/statement")
    public ResponseEntity<byte[]> statement(@PathVariable Long id,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "format", defaultValue = "csv") String format) {
        User user = currentUserProvider.getCurrentUser();
        Wallet wallet = walletAccessService.findWalletWithAccess(user, id);

        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        var page = transactionRepository.listWithFilters(wallet.getId(), null, null, start, end,
                org.springframework.data.domain.Pageable.unpaged());
        java.util.List<br.com.financeiro.entity.Transaction> transactions = page.getContent();

        ReportGenerator generator = "pdf".equalsIgnoreCase(format)
                ? new PdfReportGenerator()
                : new CsvReportGenerator();

        byte[] content = generator.generate(wallet, transactions, start, end);

        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(generator.contentType()))
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=extrato." + generator.fileExtension())
                .body(content);
    }

    @GetMapping("/{id}/insights")
    public ResponseEntity<Map<String, String>> insights(@PathVariable Long id,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        User user = currentUserProvider.getCurrentUser();
        LocalDate start = startDate != null ? startDate : LocalDate.now().withDayOfMonth(1);
        LocalDate end = endDate != null ? endDate : LocalDate.now();
        String insight = aiService.insights(user, id, start, end);
        if (insight == null) {
            return ResponseEntity.ok(Map.of("insight", ""));
        }
        return ResponseEntity.ok(Map.of("insight", insight));
    }

    private User currentUser() {
        return currentUserProvider.getCurrentUser();
    }
}