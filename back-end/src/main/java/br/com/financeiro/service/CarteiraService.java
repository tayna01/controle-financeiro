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
import br.com.financeiro.entity.Carteira;
import br.com.financeiro.entity.CarteiraMembro;
import br.com.financeiro.entity.PapelCarteira;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.BusinessException;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CarteiraMembroRepository;
import br.com.financeiro.repository.CarteiraRepository;
import br.com.financeiro.repository.UsuarioRepository;
import br.com.financeiro.util.TextUtil;

@Service
public class CarteiraService {

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private CarteiraMembroRepository membroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AcessoCarteiraService acessoCarteiraService;
    @Transactional(readOnly = true)
    public List<WalletResponse> list(Usuario usuario) {
        List<Carteira> carteiras = carteiraRepository.findByUsuarioId(usuario.getId());
        List<WalletResponse> resposta = new ArrayList<>();
        for (Carteira carteira : carteiras) {
            resposta.add(toWalletResponse(carteira));
        }
        return resposta;
    }

    @Transactional(readOnly = true)
    public WalletResponse get(Usuario usuario, Long id) {
        return toWalletResponse(acessoCarteiraService.buscarCarteiraComAcesso(usuario, id));
    }

    @Transactional
    public WalletResponse create(Usuario usuario, WalletRequest request) {
        Carteira carteira = new Carteira();
        carteira.setDono(usuario);
        carteira.setNome(TextUtil.trimToNull(request.getName()));
        carteira.setDescricao(TextUtil.trimToNull(request.getDescription()));
        carteira = carteiraRepository.save(carteira);

        CarteiraMembro dono = new CarteiraMembro();
        dono.setCarteira(carteira);
        dono.setUsuario(usuario);
        dono.setPapel(PapelCarteira.DONO);
        membroRepository.save(dono);

        return toWalletResponse(carteira);
    }

    @Transactional
    public WalletResponse update(Usuario usuario, Long id, WalletRequest request) {
        Carteira carteira = acessoCarteiraService.buscarCarteiraComAcesso(usuario, id);
        acessoCarteiraService.exigirDono(usuario, carteira);

        carteira.setNome(TextUtil.trimToNull(request.getName()));
        carteira.setDescricao(TextUtil.trimToNull(request.getDescription()));
        return toWalletResponse(carteiraRepository.save(carteira));
    }

    @Transactional
    public void delete(Usuario usuario, Long id) {
        Carteira carteira = acessoCarteiraService.buscarCarteiraComAcesso(usuario, id);
        acessoCarteiraService.exigirDono(usuario, carteira);
        carteiraRepository.delete(carteira);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(Usuario usuario, Long carteiraId) {
        acessoCarteiraService.buscarCarteiraComAcesso(usuario, carteiraId);
        return listMembros(carteiraId);
    }

    @Transactional
    public MemberResponse addMember(Usuario usuario, Long carteiraId, MemberRequest request) {
        Carteira carteira = acessoCarteiraService.buscarCarteiraComAcesso(usuario, carteiraId);
        acessoCarteiraService.exigirDono(usuario, carteira);

        Usuario convidado = usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o e-mail informado"));

        if (membroRepository.existsByCarteiraIdAndUsuarioId(carteiraId, convidado.getId())) {
            throw new ConflictException("Usuário já é membro da carteira");
        }

        CarteiraMembro membro = new CarteiraMembro();
        membro.setCarteira(carteira);
        membro.setUsuario(convidado);
        membro.setPapel(request.getRole());
        return toMemberResponse(membroRepository.save(membro));
    }

    @Transactional
    public MemberResponse updateRole(Usuario usuario, Long carteiraId, Long usuarioId, MemberRoleRequest request) {
        Carteira carteira = acessoCarteiraService.buscarCarteiraComAcesso(usuario, carteiraId);
        acessoCarteiraService.exigirDono(usuario, carteira);

        CarteiraMembro membro = buscarMembro(carteiraId, usuarioId);
        if (membro.getPapel() == PapelCarteira.DONO) {
            throw new BusinessException("Não é possível alterar o papel do dono da carteira");
        }
        membro.setPapel(request.getRole());
        return toMemberResponse(membroRepository.save(membro));
    }

    @Transactional
    public void removeMember(Usuario usuario, Long carteiraId, Long usuarioId) {
        Carteira carteira = acessoCarteiraService.buscarCarteiraComAcesso(usuario, carteiraId);
        acessoCarteiraService.exigirDono(usuario, carteira);

        CarteiraMembro membro = buscarMembro(carteiraId, usuarioId);
        if (membro.getPapel() == PapelCarteira.DONO) {
            throw new BusinessException("Não é possível remover o dono da carteira");
        }
        membroRepository.delete(membro);
    }

    private CarteiraMembro buscarMembro(Long carteiraId, Long usuarioId) {
        return membroRepository.findByCarteiraIdAndUsuarioId(carteiraId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));
    }

    private List<MemberResponse> listMembros(Long carteiraId) {
        List<CarteiraMembro> membros = membroRepository.findByCarteiraIdOrderByEntradoEmAsc(carteiraId);
        List<MemberResponse> resposta = new ArrayList<>();
        for (CarteiraMembro membro : membros) {
            resposta.add(toMemberResponse(membro));
        }
        return resposta;
    }

    private MemberResponse toMemberResponse(CarteiraMembro membro) {
        return toMemberResponse(membro.getUsuario(), membro.getPapel(), membro.getEntradoEm());
    }

    private MemberResponse toMemberResponse(Usuario usuario, PapelCarteira papel, java.time.LocalDateTime joinedAt) {
        return new MemberResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                papel.name(),
                joinedAt
        );
    }

    private WalletResponse toWalletResponse(Carteira carteira) {
        List<MemberResponse> membros = listMembros(carteira.getId());

        MemberResponse dono = ownerFallback(carteira);
        for (MemberResponse membro : membros) {
            if ("DONO".equals(membro.getRole())) {
                dono = membro;
                break;
            }
        }

        return new WalletResponse(
                carteira.getId(),
                carteira.getNome(),
                carteira.getDescricao(),
                dono,
                membros,
                carteira.getCriadoEm()
        );
    }

    private MemberResponse ownerFallback(Carteira carteira) {
        return toMemberResponse(carteira.getDono(), PapelCarteira.DONO, carteira.getCriadoEm());
    }
}
