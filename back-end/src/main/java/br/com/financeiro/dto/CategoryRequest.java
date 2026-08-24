package br.com.financeiro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 80, message = "Nome deve ter no máximo 80 caracteres")
    private String name;

    @NotBlank(message = "Tipo é obrigatório")
    @Pattern(regexp = "INCOME|EXPENSE", message = "Tipo deve ser INCOME ou EXPENSE")
    private String type;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Cor deve estar no formato #RRGGBB")
    private String color;

    @Size(max = 50, message = "Ícone deve ter no máximo 50 caracteres")
    private String icon;
}
