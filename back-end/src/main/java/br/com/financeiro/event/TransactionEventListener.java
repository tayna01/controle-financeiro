package br.com.financeiro.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import br.com.financeiro.service.WalletEventPublisher;

@Component
public class TransactionEventListener {

    private final WalletEventPublisher walletEventPublisher;

    public TransactionEventListener(WalletEventPublisher walletEventPublisher) {
        this.walletEventPublisher = walletEventPublisher;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TransactionChangedEvent event) {
        walletEventPublisher.publish(event);
    }
}