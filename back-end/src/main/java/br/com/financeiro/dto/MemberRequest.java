package br.com.financeiro.dto;

import br.com.financeiro.entity.PapelCarteira;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberRequest(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotNull(message = "Papel é obrigatório")
        PapelCarteira role
) {
}