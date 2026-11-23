package br.com.financeiro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.financeiro.dto.CategoryResponse;
import br.com.financeiro.entity.User;
import br.com.financeiro.security.CurrentUserProvider;
import br.com.financeiro.service.AiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

    public static class CategorizeRequest {
        @NotBlank(message = "Descricao e obrigatoria")
        public String description;
    }

    public static class CategorizeResponse {
        public Long categoryId;
        public String categoryName;

        public CategorizeResponse(Long categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }
    }

    @Autowired
    private AiService aiService;

    @Autowired
    private CurrentUserProvider currentUserProvider;

    @PostMapping("/categorize")
    public ResponseEntity<CategorizeResponse> categorize(@Valid @RequestBody CategorizeRequest request) {
        User user = currentUserProvider.getCurrentUser();
        CategoryResponse response = aiService.categorize(user, request.description);
        if (response == null) {
            return ResponseEntity.ok(new CategorizeResponse(null, null));
        }
        return ResponseEntity.ok(new CategorizeResponse(response.getId(), response.getName()));
    }
}