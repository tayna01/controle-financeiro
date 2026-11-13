package br.com.financeiro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.financeiro.entity.Carteira;
import br.com.financeiro.entity.CarteiraMembro;
import br.com.financeiro.entity.PapelCarteira;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CarteiraMembroRepository;
import br.com.financeiro.repository.CarteiraRepository;

@Service
@Transactional(readOnly = true)
public class AcessoCarteiraService {

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private CarteiraMembroRepository membroRepository;

    public Carteira buscarCarteiraComAcesso(Usuario usuario, Long carteiraId) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        boolean dono = carteira.getDono().getId().equals(usuario.getId());
        boolean membro = membroRepository.existsByCarteiraIdAndUsuarioId(carteiraId, usuario.getId());
        if (!dono && !membro) {
            throw new ResourceNotFoundException("Carteira não encontrada");
        }
        return carteira;
    }

    public PapelCarteira papelDoUsuario(Usuario usuario, Carteira carteira) {
        if (carteira.getDono().getId().equals(usuario.getId())) {
            return PapelCarteira.DONO;
        }
        return membroRepository.findByCarteiraIdAndUsuarioId(carteira.getId(), usuario.getId())
                .map(CarteiraMembro::getPapel)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));
    }

    public void exigirDono(Usuario usuario, Carteira carteira) {
        if (!carteira.getDono().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Somente o dono da carteira pode executar esta ação");
        }
    }

    public void exigirPermissaoEscrita(Usuario usuario, Carteira carteira) {
        PapelCarteira papel = papelDoUsuario(usuario, carteira);
        if (papel == PapelCarteira.VISUALIZADOR) {
            throw new AccessDeniedException("Você não tem permissão para alterar as transações desta carteira");
        }
    }
}
