package br.com.financeiro.dto;

import br.com.financeiro.entity.TipoTransacao;

public enum TransactionType {
    INCOME,
    EXPENSE;

    public TipoTransacao toEntity() {
        return this == INCOME ? TipoTransacao.RECEITA : TipoTransacao.DESPESA;
    }

    public static TransactionType fromEntity(TipoTransacao tipo) {
        return tipo == TipoTransacao.RECEITA ? INCOME : EXPENSE;
    }
}