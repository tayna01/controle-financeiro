package br.com.financeiro.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    private Long id;
    private String name;
    private String description;
    private MemberResponse owner;
    private List<MemberResponse> members;
    private LocalDateTime createdAt;
}
