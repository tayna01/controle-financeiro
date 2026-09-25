package br.com.financeiro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.financeiro.dto.CategoryRequest;
import br.com.financeiro.entity.Categoria;
import br.com.financeiro.entity.TipoTransacao;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.BusinessException;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.repository.CategoriaRepository;
import br.com.financeiro.repository.TransacaoRepository;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Ana");
        usuario.setEmail("ana@exemplo.com");
        return usuario;
    }

    @Test
    void criarCategoriaComNomeDuplicadoLancaConflito() {
        when(categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(1L, "Alimentacao", null)).thenReturn(true);

        assertThrows(ConflictException.class, () -> categoriaService.create(usuario(1L),
                new CategoryRequest(" Alimentacao ", "EXPENSE", "#ff9900", null)));

        verify(categoriaRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void criarCategoriaTrimAplicaNomeECor() {
        when(categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(1L, "Transporte", null)).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        categoriaService.create(usuario(1L),
                new CategoryRequest("  Transporte  ", "EXPENSE", " #00aaff ", null));

        ArgumentCaptor<Categoria> captor = ArgumentCaptor.forClass(Categoria.class);
        verify(categoriaRepository).save(captor.capture());
        assertEquals("Transporte", captor.getValue().getNome());
        assertEquals("#00aaff", captor.getValue().getCor());
        assertEquals(TipoTransacao.DESPESA, captor.getValue().getTipo());
    }

    @Test
    void excluirCategoriaComTransacoesLancaErro() {
        Categoria categoria = new Categoria();
        categoria.setId(9L);

        when(categoriaRepository.findByIdAndUsuarioId(9L, 1L)).thenReturn(Optional.of(categoria));
        when(transacaoRepository.existsByCategoriaId(9L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> categoriaService.delete(usuario(1L), 9L));

        verify(categoriaRepository, org.mockito.Mockito.never()).delete(any());
    }

    @Test
    void listarComTipoInexistenteNaoUsaFiltroDeTipo() {
        categoriaService.list(usuario(1L), null);
        verify(categoriaRepository).findByUsuarioIdOrderByNomeAsc(eq(1L));
    }
}