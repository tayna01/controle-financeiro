package br.com.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private String type;
    private BigDecimal amount;
    private String description;
    private LocalDate date;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
}
