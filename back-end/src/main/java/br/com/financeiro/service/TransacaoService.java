package br.com.financeiro.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.financeiro.dto.SummaryResponse;
import br.com.financeiro.dto.TransactionRequest;
import br.com.financeiro.dto.TransactionResponse;
import br.com.financeiro.dto.TransactionType;
import br.com.financeiro.entity.Carteira;
import br.com.financeiro.entity.CarteiraMembro;
import br.com.financeiro.entity.Categoria;
import br.com.financeiro.entity.PapelCarteira;
import br.com.financeiro.entity.TipoTransacao;
import br.com.financeiro.entity.Transacao;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CarteiraMembroRepository;
import br.com.financeiro.repository.CarteiraRepository;
import br.com.financeiro.repository.CategoriaRepository;
import br.com.financeiro.repository.TransacaoRepository;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private CarteiraMembroRepository membroRepository;

    @Autowired
    private CarteiraRepository carteiraRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> list(Usuario usuario, Long carteiraId, TransactionType type,
                                          Long categoryId, LocalDate startDate, LocalDate endDate,
                                          Pageable pageable) {
        Carteira carteira = buscarCarteiraComAcesso(usuario, carteiraId);
        TipoTransacao tipo = (type == null) ? null : type.toEntity();
        Page<Transacao> pagina = transacaoRepository.listarComFiltros(
                carteira.getId(), tipo, categoryId, startDate, endDate, traduzirOrdenacao(pageable));

        return pagina.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse get(Usuario usuario, Long carteiraId, Long id) {
        Carteira carteira = buscarCarteiraComAcesso(usuario, carteiraId);
        return toResponse(buscarTransacao(carteira.getId(), id));
    }

    private Pageable traduzirOrdenacao(Pageable pageable) {
        List<Sort.Order> ordens = new ArrayList<>();
        for (Sort.Order ordem : pageable.getSort()) {
            String propriedade = ordem.getProperty();
            if (propriedade.equals("date")) {
                propriedade = "data";
            } else if (propriedade.equals("createdAt")) {
                propriedade = "criadoEm";
            }
            ordens.add(new Sort.Order(ordem.getDirection(), propriedade));
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(ordens));
    }

    @Transactional
    public TransactionResponse create(Usuario usuario, Long carteiraId, TransactionRequest request) {
        Carteira carteira = buscarCarteiraComAcesso(usuario, carteiraId);
        exigirPermissaoEscrita(usuario, carteira);

        Transacao transacao = new Transacao();
        transacao.setCarteira(carteira);
        aplicar(transacao, request, usuario);
        transacao.setCriadoPor(usuario);

        return toResponse(transacaoRepository.save(transacao));
    }

    @Transactional
    public TransactionResponse update(Usuario usuario, Long carteiraId, Long id, TransactionRequest request) {
        Carteira carteira = buscarCarteiraComAcesso(usuario, carteiraId);
        exigirPermissaoEscrita(usuario, carteira);

        Transacao transacao = buscarTransacao(carteira.getId(), id);
        aplicar(transacao, request, usuario);

        return toResponse(transacaoRepository.save(transacao));
    }

    @Transactional
    public void delete(Usuario usuario, Long carteiraId, Long id) {
        Carteira carteira = buscarCarteiraComAcesso(usuario, carteiraId);
        exigirPermissaoEscrita(usuario, carteira);

        Transacao transacao = buscarTransacao(carteira.getId(), id);
        transacaoRepository.delete(transacao);
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(Usuario usuario, Long carteiraId, LocalDate startDate, LocalDate endDate) {
        Carteira carteira = buscarCarteiraComAcesso(usuario, carteiraId);
        Page<Transacao> pagina = transacaoRepository.listarComFiltros(
                carteira.getId(), null, null, startDate, endDate, Pageable.unpaged());
        List<Transacao> transacoes = pagina.getContent();

        SummaryResponse resposta = new SummaryResponse();
        Map<Long, SummaryResponse.CategorySummary> porCategoria = new HashMap<>();
        Map<String, SummaryResponse.MonthSummary> porMes = new TreeMap<>();

        for (Transacao transacao : transacoes) {
            boolean receita = transacao.getTipo() == TipoTransacao.RECEITA;

            if (receita) {
                resposta.setTotalIncome(resposta.getTotalIncome().add(transacao.getValor()));
            } else {
                resposta.setTotalExpense(resposta.getTotalExpense().add(transacao.getValor()));
            }
            resposta.setTransactionCount(resposta.getTransactionCount() + 1);

            if (transacao.getCategoria() != null) {
                Long idCategoria = transacao.getCategoria().getId();
                SummaryResponse.CategorySummary item = porCategoria.get(idCategoria);
                if (item == null) {
                    item = new SummaryResponse.CategorySummary(idCategoria, transacao.getCategoria().getNome(), BigDecimal.ZERO);
                    porCategoria.put(idCategoria, item);
                }
                item.setTotal(item.getTotal().add(transacao.getValor()));
            }

            String chave = String.format("%04d-%02d", transacao.getData().getYear(), transacao.getData().getMonthValue());
            SummaryResponse.MonthSummary mes = porMes.get(chave);
            if (mes == null) {
                mes = new SummaryResponse.MonthSummary(chave, BigDecimal.ZERO, BigDecimal.ZERO);
                porMes.put(chave, mes);
            }
            if (receita) {
                mes.setIncome(mes.getIncome().add(transacao.getValor()));
            } else {
                mes.setExpense(mes.getExpense().add(transacao.getValor()));
            }
        }

        resposta.setBalance(resposta.getTotalIncome().subtract(resposta.getTotalExpense()));
        resposta.getByCategory().addAll(porCategoria.values());
        resposta.getByMonth().addAll(porMes.values());
        return resposta;
    }

    private void aplicar(Transacao transacao, TransactionRequest request, Usuario usuario) {
        transacao.setTipo(request.getType().toEntity());
        transacao.setValor(request.getAmount());
        transacao.setDescricao(request.getDescription());
        transacao.setData(request.getDate());

        if (request.getCategoryId() != null) {
            Categoria categoria = categoriaRepository.findByIdAndUsuarioId(request.getCategoryId(), usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            transacao.setCategoria(categoria);
        } else {
            transacao.setCategoria(null);
        }
    }

    private Carteira buscarCarteiraComAcesso(Usuario usuario, Long carteiraId) {
        Carteira carteira = carteiraRepository.findById(carteiraId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada"));

        boolean dono = carteira.getDono().getId().equals(usuario.getId());
        boolean membro = membroRepository.existsByCarteiraIdAndUsuarioId(carteiraId, usuario.getId());
        if (!dono && !membro) {
            throw new ResourceNotFoundException("Carteira não encontrada");
        }
        return carteira;
    }

    private Transacao buscarTransacao(Long carteiraId, Long id) {
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));
        if (!transacao.getCarteira().getId().equals(carteiraId)) {
            throw new ResourceNotFoundException("Transação não encontrada");
        }
        return transacao;
    }

    private PapelCarteira papelDoUsuario(Usuario usuario, Carteira carteira) {
        if (carteira.getDono().getId().equals(usuario.getId())) {
            return PapelCarteira.DONO;
        }
        return membroRepository.findByCarteiraIdAndUsuarioId(carteira.getId(), usuario.getId())
                .map(CarteiraMembro::getPapel)
                .orElse(null);
    }

    private void exigirPermissaoEscrita(Usuario usuario, Carteira carteira) {
        PapelCarteira papel = papelDoUsuario(usuario, carteira);
        if (papel == null || papel == PapelCarteira.VISUALIZADOR) {
            throw new AccessDeniedException("Você não tem permissão para alterar as transações desta carteira");
        }
    }

    private TransactionResponse toResponse(Transacao transacao) {
        Long categoryId = null;
        String categoryName = null;
        if (transacao.getCategoria() != null) {
            categoryId = transacao.getCategoria().getId();
            categoryName = transacao.getCategoria().getNome();
        }
        return new TransactionResponse(
                transacao.getId(),
                TransactionType.fromEntity(transacao.getTipo()).name(),
                transacao.getValor(),
                transacao.getDescricao(),
                transacao.getData(),
                categoryId,
                categoryName,
                transacao.getCriadoEm()
        );
    }
}
