package br.com.financeiro.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.financeiro.dto.TransactionRequest;
import br.com.financeiro.dto.TransactionResponse;
import br.com.financeiro.entity.Category;
import br.com.financeiro.entity.Transaction;
import br.com.financeiro.entity.TransactionType;
import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CategoryRepository;
import br.com.financeiro.repository.TransactionRepository;
import br.com.financeiro.util.TextUtil;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private WalletAccessService walletAccessService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> list(User user, Long walletId, TransactionType type,
                                          Long categoryId, LocalDate startDate, LocalDate endDate,
                                          Pageable pageable) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        Page<Transaction> page = transactionRepository.listWithFilters(
                wallet.getId(), type, categoryId, startDate, endDate, translateSorting(pageable));

        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(User user, Long walletId, Long id) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        return toResponse(findTransaction(wallet.getId(), id));
    }

    private Pageable translateSorting(Pageable pageable) {
        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            if (property.equals("date")) {
                property = "date";
            } else if (property.equals("createdAt")) {
                property = "createdAt";
            }
            orders.add(new Sort.Order(order.getDirection(), property));
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(orders));
    }

    @Transactional
    public TransactionResponse create(User user, Long walletId, TransactionRequest request) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        walletAccessService.requireWritePermission(user, wallet);

        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        apply(transaction, request, user);
        transaction.setCreatedBy(user);

        Transaction saved = transactionRepository.save(transaction);
        eventPublisher.publishEvent(new br.com.financeiro.event.TransactionChangedEvent(
                wallet.getId(), saved.getId(), "CREATED", user.getName()));
        return toResponse(saved);
    }

    @Transactional
    public TransactionResponse update(User user, Long walletId, Long id, TransactionRequest request) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        walletAccessService.requireWritePermission(user, wallet);

        Transaction transaction = findTransaction(wallet.getId(), id);
        apply(transaction, request, user);

        Transaction saved = transactionRepository.save(transaction);
        eventPublisher.publishEvent(new br.com.financeiro.event.TransactionChangedEvent(
                wallet.getId(), saved.getId(), "UPDATED", user.getName()));
        return toResponse(saved);
    }

    @Transactional
    public void delete(User user, Long walletId, Long id) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        walletAccessService.requireWritePermission(user, wallet);

        Transaction transaction = findTransaction(wallet.getId(), id);
        Long transactionId = transaction.getId();
        transactionRepository.delete(transaction);
        eventPublisher.publishEvent(new br.com.financeiro.event.TransactionChangedEvent(
                wallet.getId(), transactionId, "DELETED", user.getName()));
    }

    private void apply(Transaction transaction, TransactionRequest request, User user) {
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(TextUtil.trimToNull(request.getDescription()));
        transaction.setDate(request.getDate());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            transaction.setCategory(category);
        } else {
            transaction.setCategory(null);
        }
    }

    private Transaction findTransaction(Long walletId, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));
        if (!transaction.getWallet().getId().equals(walletId)) {
            throw new ResourceNotFoundException("Transaction not found");
        }
        return transaction;
    }

    private TransactionResponse toResponse(Transaction transaction) {
        Long categoryId = null;
        String categoryName = null;
        if (transaction.getCategory() != null) {
            categoryId = transaction.getCategory().getId();
            categoryName = transaction.getCategory().getName();
        }
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType().name(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                categoryId,
                categoryName,
                transaction.getCreatedAt()
        );
    }
}