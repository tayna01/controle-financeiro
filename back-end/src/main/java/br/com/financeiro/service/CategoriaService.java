package br.com.financeiro.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(Usuario usuario, TransactionType type) {
        List<Categoria> categorias = (type == null)
                ? categoriaRepository.findByUsuarioIdOrderByNomeAsc(usuario.getId())
                : categoriaRepository.findByUsuarioIdAndTipoOrderByNomeAsc(usuario.getId(), type.toEntity());

        List<CategoryResponse> resposta = new ArrayList<>();
        for (Categoria categoria : categorias) {
            resposta.add(toResponse(categoria));
        }
        return resposta;
    }

    @Transactional
    public CategoryResponse create(Usuario usuario, CategoryRequest request) {
        verificarNomeDuplicado(usuario.getId(), request.getName(), null);

        Categoria categoria = new Categoria();
        categoria.setUsuario(usuario);
        aplicar(categoria, request);

        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoryResponse update(Usuario usuario, Long id, CategoryRequest request) {
        Categoria categoria = buscar(usuario, id);
        verificarNomeDuplicado(usuario.getId(), request.getName(), id);

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
        if (categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(usuarioId, nome, idIgnorar)) {
            throw new ConflictException("Já existe uma categoria com este nome");
        }
    }

    private void aplicar(Categoria categoria, CategoryRequest request) {
        categoria.setNome(request.getName());
        categoria.setTipo(TransactionType.valueOf(request.getType()).toEntity());
        categoria.setCor(request.getColor());
        categoria.setIcone(request.getIcon());
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
