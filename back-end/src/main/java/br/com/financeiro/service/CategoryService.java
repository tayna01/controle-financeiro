package br.com.financeiro.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.financeiro.dto.CategoryRequest;
import br.com.financeiro.dto.CategoryResponse;
import br.com.financeiro.entity.TransactionType;
import br.com.financeiro.entity.Category;
import br.com.financeiro.entity.User;
import br.com.financeiro.exception.BusinessException;
import br.com.financeiro.exception.ConflictException;
import br.com.financeiro.exception.ResourceNotFoundException;
import br.com.financeiro.repository.CategoryRepository;
import br.com.financeiro.repository.TransactionRepository;
import br.com.financeiro.util.TextUtil;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> list(User user, TransactionType type) {
        List<Category> categories = (type == null)
                ? categoryRepository.findByUserIdOrderByNameAsc(user.getId())
                : categoryRepository.findByUserIdAndTypeOrderByNameAsc(user.getId(), type);

        List<CategoryResponse> response = new ArrayList<>();
        for (Category category : categories) {
            response.add(toResponse(category));
        }
        return response;
    }

    @Transactional
    public CategoryResponse create(User user, CategoryRequest request) {
        String name = TextUtil.trimToNull(request.getName());
        checkDuplicateName(user.getId(), name, null);

        Category category = new Category();
        category.setUser(user);
        apply(category, request);

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(User user, Long id, CategoryRequest request) {
        Category category = find(user, id);
        String name = TextUtil.trimToNull(request.getName());
        checkDuplicateName(user.getId(), name, id);

        apply(category, request);
        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void delete(User user, Long id) {
        Category category = find(user, id);
        if (transactionRepository.existsByCategoryId(id)) {
            throw new BusinessException("Cannot delete: category has linked transactions");
        }
        categoryRepository.delete(category);
    }

    private Category find(User user, Long id) {
        return categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private void checkDuplicateName(Long userId, String name, Long idToIgnore) {
        if (categoryRepository.existsByUserIdAndNameIgnoreCase(userId, name, idToIgnore)) {
            throw new ConflictException("A category with this name already exists");
        }
    }

    private void apply(Category category, CategoryRequest request) {
        category.setName(TextUtil.trimToNull(request.getName()));
        category.setType(request.getType());
        category.setColor(TextUtil.trimToNull(request.getColor()));
        category.setIcon(TextUtil.trimToNull(request.getIcon()));
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType().name(),
                category.getColor(),
                category.getIcon(),
                category.getCreatedAt()
        );
    }
}