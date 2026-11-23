package br.com.financeiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.entity.WalletMember;
import br.com.financeiro.entity.WalletRole;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.WalletMemberRepository;
import br.com.financeiro.repository.WalletRepository;

@Service
@Transactional(readOnly = true)
public class WalletAccessService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMemberRepository memberRepository;

    public Wallet findWalletWithAccess(User user, Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        boolean owner = wallet.getOwner().getId().equals(user.getId());
        boolean member = memberRepository.existsByWalletIdAndUserId(walletId, user.getId());
        if (!owner && !member) {
            throw new ResourceNotFoundException("Wallet not found");
        }
        return wallet;
    }

    public WalletRole roleOf(User user, Wallet wallet) {
        if (wallet.getOwner().getId().equals(user.getId())) {
            return WalletRole.OWNER;
        }
        return memberRepository.findByWalletIdAndUserId(wallet.getId(), user.getId())
                .map(WalletMember::getRole)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));
    }

    public void requireOwner(User user, Wallet wallet) {
        if (!wallet.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Somente o dono da carteira pode executar esta ação");
        }
    }

    public void requireWritePermission(User user, Wallet wallet) {
        WalletRole role = roleOf(user, wallet);
        if (role == WalletRole.VIEWER) {
            throw new AccessDeniedException("Você não tem permissão para alterar as transações desta carteira");
        }
    }
}