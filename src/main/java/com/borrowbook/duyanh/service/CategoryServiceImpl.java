package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.CategoryCreationRequest;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category createCategory(CategoryCreationRequest categoryCreationRequest) {
        if(categoryRepository.existsCategoryActiveByCategoryName(categoryCreationRequest.getCategoryName()) != null) {
            throw new AppException(ErrorCode.EXISTED_CATEGORY_NAME);
        }
        Category category = new Category();
        category.setCategoryName(categoryCreationRequest.getCategoryName());
        category.setStatus(categoryCreationRequest.getStatus());
        categoryRepository.save(category);
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(CategoryCreationRequest categoryCreationRequest, int categoryId) {
        Category category = getCategoryById(categoryId);
        category.setCategoryName(categoryCreationRequest.getCategoryName());
        category.setStatus(categoryCreationRequest.getStatus());
        categoryRepository.saveAndFlush(category);
        return category;
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.getAllCategoryActive();
    }

    @Override
    public Category getCategoryById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.valueOf(ErrorCode.EXISTED_CATEGORY_NAME)));
    }

    public boolean deleteCategoryById(int cid) {
        int affectedCategories = categoryRepository.deleteCategory(cid);
        if (affectedCategories > 0) {
            int affectedBooks = categoryRepository.deleteBooksByCategory(cid);
            return affectedBooks > 0;
        }
        return false;
    }
}
