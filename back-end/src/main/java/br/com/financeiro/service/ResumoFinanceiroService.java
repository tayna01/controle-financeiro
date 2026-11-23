package br.com.financeiro.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.financeiro.dto.SummaryResponse;
import br.com.financeiro.entity.Carteira;
import br.com.financeiro.entity.TipoTransacao;
import br.com.financeiro.entity.Transacao;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.repository.TransacaoRepository;

@Service
@Transactional(readOnly = true)
public class ResumoFinanceiroService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private AcessoCarteiraService acessoCarteiraService;

    public SummaryResponse summary(Usuario usuario, Long carteiraId, LocalDate startDate, LocalDate endDate) {
        Carteira carteira = acessoCarteiraService.buscarCarteiraComAcesso(usuario, carteiraId);
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
}
