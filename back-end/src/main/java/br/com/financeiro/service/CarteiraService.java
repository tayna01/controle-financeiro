package br.com.financeiro.service;

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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarteiraService {

    private final CarteiraRepository carteiraRepository;
    private final CarteiraMembroRepository membroRepository;
    private final UsuarioRepository usuarioRepository;

    public CarteiraService(CarteiraRepository carteiraRepository,
                           CarteiraMembroRepository membroRepository,
                           UsuarioRepository usuarioRepository) {
        this.carteiraRepository = carteiraRepository;
        this.membroRepository = membroRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<WalletResponse> list(Usuario usuario) {
        return carteiraRepository.findByUsuarioId(usuario.getId()).stream()
                .map(this::toWalletResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WalletResponse get(Usuario usuario, Long id) {
        return toWalletResponse(buscarComAcesso(usuario, id));
    }

    @Transactional
    public WalletResponse create(Usuario usuario, WalletRequest request) {
        Carteira carteira = new Carteira();
        carteira.setDono(usuario);
        carteira.setNome(request.name());
        carteira.setDescricao(request.description());
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
        Carteira carteira = buscarComAcesso(usuario, id);
        exigirDono(carteira, usuario);

        carteira.setNome(request.name());
        carteira.setDescricao(request.description());
        return toWalletResponse(carteiraRepository.save(carteira));
    }

    @Transactional
    public void delete(Usuario usuario, Long id) {
        Carteira carteira = buscarComAcesso(usuario, id);
        exigirDono(carteira, usuario);
        carteiraRepository.delete(carteira);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> listMembers(Usuario usuario, Long carteiraId) {
        buscarComAcesso(usuario, carteiraId);
        return listMembros(carteiraId);
    }

    @Transactional
    public MemberResponse addMember(Usuario usuario, Long carteiraId, MemberRequest request) {
        Carteira carteira = buscarComAcesso(usuario, carteiraId);
        exigirDono(carteira, usuario);

        Usuario convidado = usuarioRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o e-mail informado"));

        if (membroRepository.existsByCarteiraIdAndUsuarioId(carteiraId, convidado.getId())) {
            throw new ConflictException("Usuário já é membro da carteira");
        }

        CarteiraMembro membro = new CarteiraMembro();
        membro.setCarteira(carteira);
        membro.setUsuario(convidado);
        membro.setPapel(request.role());
        return toMemberResponse(membroRepository.save(membro));
    }

    @Transactional
    public MemberResponse updateRole(Usuario usuario, Long carteiraId, Long usuarioId, MemberRoleRequest request) {
        Carteira carteira = buscarComAcesso(usuario, carteiraId);
        exigirDono(carteira, usuario);

        CarteiraMembro membro = buscarMembro(carteiraId, usuarioId);
        if (membro.getPapel() == PapelCarteira.DONO) {
            throw new BusinessException("Não é possível alterar o papel do dono da carteira");
        }
        membro.setPapel(request.role());
        return toMemberResponse(membroRepository.save(membro));
    }

    @Transactional
    public void removeMember(Usuario usuario, Long carteiraId, Long usuarioId) {
        Carteira carteira = buscarComAcesso(usuario, carteiraId);
        exigirDono(carteira, usuario);

        CarteiraMembro membro = buscarMembro(carteiraId, usuarioId);
        if (membro.getPapel() == PapelCarteira.DONO) {
            throw new BusinessException("Não é possível remover o dono da carteira");
        }
        membroRepository.delete(membro);
    }

    private Carteira buscarComAcesso(Usuario usuario, Long carteiraId) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        boolean dono = carteira.getDono().getId().equals(usuario.getId());
        boolean membro = membroRepository.existsByCarteiraIdAndUsuarioId(carteiraId, usuario.getId());
        if (!dono && !membro) {
            throw new ResourceNotFoundException("Carteira não encontrada");
        }
        return carteira;
    }

    private CarteiraMembro buscarMembro(Long carteiraId, Long usuarioId) {
        return membroRepository.findByCarteiraIdAndUsuarioId(carteiraId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));
    }

    private void exigirDono(Carteira carteira, Usuario usuario) {
        if (!carteira.getDono().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Somente o dono da carteira pode executar esta ação");
        }
    }

    private List<MemberResponse> listMembros(Long carteiraId) {
        return membroRepository.findByCarteiraIdOrderByEntradoEmAsc(carteiraId).stream()
                .map(this::toMemberResponse)
                .toList();
    }

    private MemberResponse toMemberResponse(CarteiraMembro membro) {
        Usuario usuario = membro.getUsuario();
        return new MemberResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                membro.getPapel().name(),
                membro.getEntradoEm()
        );
    }

    private WalletResponse toWalletResponse(Carteira carteira) {
        List<MemberResponse> membros = listMembros(carteira.getId());
        MemberResponse dono = membros.stream()
                .filter(m -> "DONO".equals(m.role()))
                .findFirst()
                .orElseGet(() -> ownerFallback(carteira));

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
        return new MemberResponse(
                carteira.getDono().getId(),
                carteira.getDono().getNome(),
                carteira.getDono().getEmail(),
                PapelCarteira.DONO.name(),
                carteira.getCriadoEm()
        );
    }
}