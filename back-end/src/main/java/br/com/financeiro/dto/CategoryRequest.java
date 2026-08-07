package br.com.financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 80, message = "Nome deve ter no máximo 80 caracteres")
        String name,

        @NotBlank(message = "Tipo é obrigatório")
        @Pattern(regexp = "INCOME|EXPENSE", message = "Tipo deve ser INCOME ou EXPENSE")
        String type,

        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato #RRGGBB")
        String color,

        @Size(max = 50, message = "Ícone deve ter no máximo 50 caracteres")
        String icon
) {
}