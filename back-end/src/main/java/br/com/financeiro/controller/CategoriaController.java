package br.com.financeiro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.financeiro.dto.CategoryRequest;
import br.com.financeiro.dto.CategoryResponse;
import br.com.financeiro.dto.TransactionType;
import br.com.financeiro.entity.Usuario;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.CategoriaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list(@RequestParam(value = "type", required = false) TransactionType type) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(categoriaService.list(usuario, type));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.create(usuario, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(categoriaService.update(usuario, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Usuario usuario = currentUserProvider.getCurrentUser();
        categoriaService.delete(usuario, id);
        return ResponseEntity.noContent().build();
    }
}