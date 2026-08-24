package br.com.financeiro.dto;

import br.com.financeiro.entity.PapelCarteira;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleRequest {

    @NotNull(message = "Papel é obrigatório")
    private PapelCarteira role;
}
