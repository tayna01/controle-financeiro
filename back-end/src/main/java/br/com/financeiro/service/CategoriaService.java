package br.com.financeiro.service;

import br.com.financeiro.dto.CategoryRequest;
import br.com.financeiro.dto.CategoryResponse;
import br.com.financeiro.dto.TransactionType;
import br.com.financeiro.entity.Categoria;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.exception.BusinessException;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CategoriaRepository;
import br.com.financeiro.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final TransacaoRepository transacaoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            TransacaoRepository transacaoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.transacaoRepository = transacaoRepository;
    }

@Transactional(readOnly = true)
    public List<CategoryResponse> list(Usuario usuario, TransactionType type) {
        List<Categoria> categorias = (type == null)
                ? categoriaRepository.findByUsuarioIdOrderByNomeAsc(usuario.getId())
                : categoriaRepository.findByUsuarioIdAndTipoOrderByNomeAsc(usuario.getId(), type.toEntity());
        return categorias.stream().map(this::toResponse).toList();
    }

    @Transactional
    public CategoryResponse create(Usuario usuario, CategoryRequest request) {
        verificarNomeDuplicado(usuario.getId(), request.name(), null);

        Categoria categoria = new Categoria();
        categoria.setUsuario(usuario);
        aplicar(categoria, request);

        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoryResponse update(Usuario usuario, Long id, CategoryRequest request) {
        Categoria categoria = buscar(usuario, id);
        verificarNomeDuplicado(usuario.getId(), request.name(), id);

        aplicar(categoria, request);
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void delete(Usuario usuario, Long id) {
        Categoria categoria = buscar(usuario, id);
        if (transacaoRepository.existsByCategoriaId(id)) {
            throw new BusinessException("Não é possível excluir: categoria possui transações vinculadas");
        }
        categoriaRepository.delete(categoria);
    }

    private Categoria buscar(Usuario usuario, Long id) {
        return categoriaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
    }

    private void verificarNomeDuplicado(Long usuarioId, String nome, Long idIgnorar) {
        if (categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(usuarioId, nome)) {
            throw new ConflictException("Já existe uma categoria com este nome");
        }
    }

    private void aplicar(Categoria categoria, CategoryRequest request) {
        categoria.setNome(request.name());
        categoria.setTipo(TransactionType.valueOf(request.type()).toEntity());
        categoria.setCor(request.color());
        categoria.setIcone(request.icon());
    }

    private CategoryResponse toResponse(Categoria categoria) {
        return new CategoryResponse(
                categoria.getId(),
                categoria.getNome(),
                TransactionType.fromEntity(categoria.getTipo()).name(),
                categoria.getCor(),
                categoria.getIcone(),
                categoria.getCriadoEm()
        );
    }
}