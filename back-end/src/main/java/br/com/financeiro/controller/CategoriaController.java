package br.com.financeiro.controller;

import br.com.financeiro.dto.CategoryRequest;
import br.com.financeiro.dto.CategoryResponse;
import br.com.financeiro.dto.TransactionType;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GetMapping
    public List<CategoryResponse> list(@RequestParam(value = "type", required = false) TransactionType type) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return categoriaService.list(usuario, type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CategoryRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return categoriaService.create(usuario, request);
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return categoriaService.update(usuario, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        categoriaService.delete(usuario, id);
    }
}