package br.com.financeiro.dto;

import java.time.LocalDateTime;

public record CategoryResponse(
        Long id,
        String name,
        String type,
        String color,
        String icon,
        LocalDateTime createdAt
) {
}