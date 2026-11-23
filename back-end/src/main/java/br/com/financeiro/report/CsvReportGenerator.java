package br.com.financeiro.report;

import br.com.financeiro.entity.Transaction;
import br.com.financeiro.entity.Wallet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CsvReportGenerator implements ReportGenerator {

    @Override
    public byte[] generate(Wallet wallet, java.util.List<Transaction> transactions,
                           LocalDate startDate, LocalDate endDate) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        writer.println("Carteira;Tipo;Valor;Descricao;Data;Categoria");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Transaction transaction : transactions) {
            writer.printf("%s;%s;%s;%s;%s;%s%n",
                    escape(wallet.getName()),
                    transaction.getType().name(),
                    transaction.getAmount().toPlainString(),
                    escape(transaction.getDescription()),
                    transaction.getDate().format(formatter),
                    transaction.getCategory() != null ? escape(transaction.getCategory().getName()) : "");
        }
        writer.flush();
        return out.toByteArray();
    }

    @Override
    public String contentType() {
        return "text/csv";
    }

    @Override
    public String fileExtension() {
        return "csv";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(";", ",");
    }
}