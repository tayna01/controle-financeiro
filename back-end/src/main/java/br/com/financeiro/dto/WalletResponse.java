package br.com.financeiro.dto;

import java.time.LocalDateTime;
import java.util.List;

public record WalletResponse(
        Long id,
        String name,
        String description,
        MemberResponse owner,
        List<MemberResponse> members,
        LocalDateTime createdAt
) {
}