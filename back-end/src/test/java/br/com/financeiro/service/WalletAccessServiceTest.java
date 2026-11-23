package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.entity.WalletMember;
import br.com.financeiro.entity.WalletRole;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.WalletMemberRepository;
import br.com.financeiro.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
class WalletAccessServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletMemberRepository memberRepository;

    @InjectMocks
    private WalletAccessService walletAccessService;

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("User " + id);
        user.setEmail("user" + id + "@example.com");
        return user;
    }

    private Wallet wallet(User owner, Long id) {
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setOwner(owner);
        wallet.setName("Wallet");
        return wallet;
    }

    @Test
    void noAccessThrowsNotFound() {
        User owner = user(1L);
        User outsider = user(99L);
        Wallet wallet = wallet(owner, 10L);

        org.mockito.Mockito.when(walletRepository.findById(10L)).thenReturn(Optional.of(wallet));
        org.mockito.Mockito.when(memberRepository.existsByWalletIdAndUserId(10L, 99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> walletAccessService.findWalletWithAccess(outsider, 10L));
    }

    @Test
    void viewerHasNoWritePermission() {
        User owner = user(1L);
        User viewer = user(2L);
        Wallet wallet = wallet(owner, 10L);

        WalletMember member = new WalletMember();
        member.setRole(WalletRole.VIEWER);

        org.mockito.Mockito.when(memberRepository.findByWalletIdAndUserId(10L, 2L))
                .thenReturn(Optional.of(member));

        assertEquals(WalletRole.VIEWER, walletAccessService.roleOf(viewer, wallet));
        assertThrows(AccessDeniedException.class,
                () -> walletAccessService.requireWritePermission(viewer, wallet));
    }

    @Test
    void editorHasWritePermission() {
        User owner = user(1L);
        User editor = user(2L);
        Wallet wallet = wallet(owner, 10L);

        WalletMember member = new WalletMember();
        member.setRole(WalletRole.EDITOR);

        org.mockito.Mockito.when(memberRepository.findByWalletIdAndUserId(10L, 2L))
                .thenReturn(Optional.of(member));

        assertEquals(WalletRole.EDITOR, walletAccessService.roleOf(editor, wallet));
        assertDoesNotThrow(() -> walletAccessService.requireWritePermission(editor, wallet));
    }

    @Test
    void ownerHasAccessAndPermission() {
        User owner = user(1L);
        Wallet wallet = wallet(owner, 10L);

        assertEquals(WalletRole.OWNER, walletAccessService.roleOf(owner, wallet));
        assertDoesNotThrow(() -> walletAccessService.requireOwner(owner, wallet));
        assertDoesNotThrow(() -> walletAccessService.requireWritePermission(owner, wallet));
    }
}