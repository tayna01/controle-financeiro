package br.com.financeiro.dto;

import java.time.LocalDateTime;

public record MemberResponse(
        Long userId,
        String name,
        String email,
        String role,
        LocalDateTime joinedAt
) {
}