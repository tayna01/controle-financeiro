package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.financeiro.dto.MemberRequest;
import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.entity.WalletRole;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.UserRepository;
import br.com.financeiro.repository.WalletMemberRepository;
import br.com.financeiro.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMemberRepository memberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletAccessService walletAccessService;

    @InjectMocks
    private WalletService walletService;

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }

    @Test
    void addMemberWithUnknownUserThrowsNotFound() {
        User owner = user(1L);
        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(owner);

        when(walletAccessService.findWalletWithAccess(owner, 10L)).thenReturn(wallet);
        when(userRepository.findByEmailIgnoreCase("who@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> walletService
                .addMember(owner, 10L, new MemberRequest("who@example.com", WalletRole.EDITOR)));
    }

    @Test
    void addExistingMemberThrowsConflict() {
        User owner = user(1L);
        User guest = user(2L);
        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(owner);

        when(walletAccessService.findWalletWithAccess(owner, 10L)).thenReturn(wallet);
        when(userRepository.findByEmailIgnoreCase("guest@example.com")).thenReturn(Optional.of(guest));
        when(memberRepository.existsByWalletIdAndUserId(10L, 2L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> walletService
                .addMember(owner, 10L, new MemberRequest("guest@example.com", WalletRole.EDITOR)));
    }

    @Test
    void accessOtherUserWalletThrowsNotFound() {
        User owner = user(1L);
        User outsider = user(9L);
        Wallet wallet = new Wallet();
        wallet.setId(10L);
        wallet.setOwner(owner);

        when(walletAccessService.findWalletWithAccess(outsider, 10L))
                .thenThrow(new ResourceNotFoundException("Wallet not found"));

        assertThrows(ResourceNotFoundException.class, () -> walletService.get(outsider, 10L));
    }
}