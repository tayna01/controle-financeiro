package br.com.financeiro.report;

import br.com.financeiro.entity.Wallet;

import java.time.LocalDate;

public interface ReportGenerator {

    byte[] generate(Wallet wallet, java.util.List<br.com.financeiro.entity.Transaction> transactions,
                   LocalDate startDate, LocalDate endDate);

    String contentType();

    String fileExtension();
}