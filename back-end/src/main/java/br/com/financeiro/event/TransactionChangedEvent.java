package br.com.financeiro.event;

public class TransactionChangedEvent {

    private final Long walletId;
    private final Long transactionId;
    private final String action;
    private final String actorName;

    public TransactionChangedEvent(Long walletId, Long transactionId, String action, String actorName) {
        this.walletId = walletId;
        this.transactionId = transactionId;
        this.action = action;
        this.actorName = actorName;
    }

    public Long getWalletId() {
        return walletId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public String getAction() {
        return action;
    }

    public String getActorName() {
        return actorName;
    }
}