package br.com.financeiro.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.financeiro.dto.MemberRequest;
import br.com.financeiro.dto.MemberResponse;
import br.com.financeiro.dto.MemberRoleRequest;
import br.com.financeiro.dto.WalletRequest;
import br.com.financeiro.dto.WalletResponse;
import br.com.financeiro.entity.User;
import br.com.financeiro.entity.Wallet;
import br.com.financeiro.entity.WalletMember;
import br.com.financeiro.entity.WalletRole;
import br.com.financeiro.exception.BusinessException;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.UserRepository;
import br.com.financeiro.repository.WalletMemberRepository;
import br.com.financeiro.repository.WalletRepository;
import br.com.financeiro.util.TextUtil;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletAccessService walletAccessService;

    @Transactional(readOnly = true)
    public List<WalletResponse> list(User user) {
        List<Wallet> wallets = walletRepository.findByUserId(user.getId());
        List<WalletResponse> response = new ArrayList<>();
        for (Wallet wallet : wallets) {
            response.add(toWalletResponse(wallet));
        }
        return response;
    }

    @Transactional(readOnly = true)
    public WalletResponse get(User user, Long id) {
        return toWalletResponse(walletAccessService.findWalletWithAccess(user, id));
    }

    @Transactional
    public WalletResponse create(User user, WalletRequest request) {
        Wallet wallet = new Wallet();
        wallet.setOwner(user);
        wallet.setName(TextUtil.trimToNull(request.getName()));
        wallet.setDescription(TextUtil.trimToNull(request.getDescription()));
        wallet = walletRepository.save(wallet);

        WalletMember owner = new WalletMember();
        owner.setWallet(wallet);
        owner.setUser(user);
        owner.setRole(WalletRole.OWNER);
        memberRepository.save(owner);

        return toWalletResponse(wallet);
    }

    @Transactional
    public WalletResponse update(User user, Long id, WalletRequest request) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, id);
        walletAccessService.requireOwner(user, wallet);

        wallet.setName(TextUtil.trimToNull(request.getName()));
        wallet.setDescription(TextUtil.trimToNull(request.getDescription()));
        return toWalletResponse(walletRepository.save(wallet));
    }

    @Transactional
    public void delete(User user, Long id) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, id);
        walletAccessService.requireOwner(user, wallet);
        walletRepository.delete(wallet);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(User user, Long walletId) {
        walletAccessService.findWalletWithAccess(user, walletId);
        return listMembers(walletId);
    }

    @Transactional
    public MemberResponse addMember(User user, Long walletId, MemberRequest request) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        walletAccessService.requireOwner(user, wallet);

        User guest = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o e-mail informado"));

        if (memberRepository.existsByWalletIdAndUserId(walletId, guest.getId())) {
            throw new ConflictException("Usuário já é membro da carteira");
        }

        WalletMember member = new WalletMember();
        member.setWallet(wallet);
        member.setUser(guest);
        member.setRole(request.getRole());
        return toMemberResponse(memberRepository.save(member));
    }

    @Transactional
    public MemberResponse updateRole(User user, Long walletId, Long userId, MemberRoleRequest request) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        walletAccessService.requireOwner(user, wallet);

        WalletMember member = findMember(walletId, userId);
        if (member.getRole() == WalletRole.OWNER) {
            throw new BusinessException("Não é possível alterar o papel do dono da carteira");
        }
        member.setRole(request.getRole());
        return toMemberResponse(memberRepository.save(member));
    }

    @Transactional
    public void removeMember(User user, Long walletId, Long userId) {
        Wallet wallet = walletAccessService.findWalletWithAccess(user, walletId);
        walletAccessService.requireOwner(user, wallet);

        WalletMember member = findMember(walletId, userId);
        if (member.getRole() == WalletRole.OWNER) {
            throw new BusinessException("Não é possível remover o dono da carteira");
        }
        memberRepository.delete(member);
    }

    private WalletMember findMember(Long walletId, Long userId) {
        return memberRepository.findByWalletIdAndUserId(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));
    }

    private List<MemberResponse> listMembers(Long walletId) {
        List<WalletMember> members = memberRepository.findByWalletIdOrderByJoinedAtAsc(walletId);
        List<MemberResponse> response = new ArrayList<>();
        for (WalletMember member : members) {
            response.add(toMemberResponse(member));
        }
        return response;
    }

    private MemberResponse toMemberResponse(WalletMember member) {
        return toMemberResponse(member.getUser(), member.getRole(), member.getJoinedAt());
    }

    private MemberResponse toMemberResponse(User user, WalletRole role, java.time.LocalDateTime joinedAt) {
        return new MemberResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                role.name(),
                joinedAt
        );
    }

    private WalletResponse toWalletResponse(Wallet wallet) {
        List<MemberResponse> members = listMembers(wallet.getId());

        MemberResponse owner = toMemberResponse(wallet.getOwner(), WalletRole.OWNER, wallet.getCreatedAt());
        for (MemberResponse member : members) {
            if ("OWNER".equals(member.getRole())) {
                owner = member;
                break;
            }
        }

        return new WalletResponse(
                wallet.getId(),
                wallet.getName(),
                wallet.getDescription(),
                owner,
                members,
                wallet.getCreatedAt()
        );
    }
}