package br.com.financeiro.report;

import br.com.financeiro.entity.Transaction;
import br.com.financeiro.entity.Wallet;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PdfReportGenerator implements ReportGenerator {

    @Override
    public byte[] generate(Wallet wallet, java.util.List<Transaction> transactions,
                           LocalDate startDate, LocalDate endDate) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("Extrato - " + wallet.getName()));
            document.add(new Paragraph("Periodo: " + startDate + " a " + endDate));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.addCell("Tipo");
            table.addCell("Valor");
            table.addCell("Descricao");
            table.addCell("Data");
            table.addCell("Categoria");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Transaction transaction : transactions) {
                table.addCell(transaction.getType().name());
                table.addCell(transaction.getAmount().toPlainString());
                table.addCell(transaction.getDescription() != null ? transaction.getDescription() : "");
                table.addCell(transaction.getDate().format(formatter));
                table.addCell(transaction.getCategory() != null ? transaction.getCategory().getName() : "");
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao gerar relatorio PDF", ex);
        }
    }

    @Override
    public String contentType() {
        return "application/pdf";
    }

    @Override
    public String fileExtension() {
        return "pdf";
    }
}