package br.com.financeiro.dto;

import br.com.financeiro.entity.PapelCarteira;
import jakarta.validation.constraints.NotNull;

public record MemberRoleRequest(
        @NotNull(message = "Papel é obrigatório")
        PapelCarteira role
) {
}