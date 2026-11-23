package br.com.financeiro.dto;

import br.com.financeiro.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 80, message = "Name must have at most 80 characters")
    private String name;

    @NotNull(message = "Type is required")
    private TransactionType type;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be in #RRGGBB format")
    private String color;

    @Size(max = 50, message = "Icon must have at most 50 characters")
    private String icon;
}