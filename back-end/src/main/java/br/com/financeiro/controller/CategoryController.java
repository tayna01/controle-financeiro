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
import br.com.financeiro.entity.TransactionType;
import br.com.financeiro.entity.User;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.CategoryService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list(@RequestParam(value = "type", required = false) TransactionType type) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(categoryService.list(user, type));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(user, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        User user = currentUserProvider.getCurrentUser();
        return ResponseEntity.ok(categoryService.update(user, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        User user = currentUserProvider.getCurrentUser();
        categoryService.delete(user, id);
        return ResponseEntity.noContent().build();
    }
}