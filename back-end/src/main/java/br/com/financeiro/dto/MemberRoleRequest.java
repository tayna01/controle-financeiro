package br.com.financeiro.dto;

import br.com.financeiro.entity.WalletRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRoleRequest {

    @NotNull(message = "Papel é obrigatório")
    private WalletRole role;
}