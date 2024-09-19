package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.CategoryCreationRequest;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category createCategory(CategoryCreationRequest categoryCreationRequest) {
        if(categoryRepository.existsCategoryActiveByCategoryName(categoryCreationRequest.getCategoryName()) != null
        && categoryCreationRequest.getStatus().equalsIgnoreCase("ACTIVE")) {
            throw new AppException(ErrorCode.EXISTED_CATEGORY_NAME);
        } else if(categoryRepository.existsCategoryActiveByCategoryName(categoryCreationRequest.getCategoryName()) != null
                && categoryCreationRequest.getStatus().equalsIgnoreCase("DELETE")) {
            throw new AppException(ErrorCode.CATEGORY_EXIST_DELETED);
        }
        Category category = new Category();
        category.setCategoryName(categoryCreationRequest.getCategoryName());
        category.setStatus(categoryCreationRequest.getStatus());

        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    @Modifying
    public Category updateCategory(CategoryCreationRequest categoryCreationRequest, int categoryId) {
        if(categoryRepository.existsCategoryActiveByCategoryName(categoryCreationRequest.getCategoryName()) != null
                && categoryCreationRequest.getStatus().equalsIgnoreCase("ACTIVE")) {
            throw new AppException(ErrorCode.EXISTED_CATEGORY_NAME);
        } else if(categoryRepository.existsCategoryActiveByCategoryName(categoryCreationRequest.getCategoryName()) != null
                && categoryCreationRequest.getStatus().equalsIgnoreCase("DELETE")) {
            throw new AppException(ErrorCode.CATEGORY_EXIST_DELETED);
        }
        Category category = getCategoryById(categoryId);
        category.setCategoryName(categoryCreationRequest.getCategoryName());
        category.setStatus(categoryCreationRequest.getStatus());

        return categoryRepository.saveAndFlush(category);
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.getAllCategoryActive();
    }

    @Override
    public List<Category> getAllCategoryDelete() {
        return categoryRepository.getAllCategoryDelete();
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

    @Override
    public boolean activeCategory(int cid) {
        return categoryRepository.activeCategory(cid) > 0;
    }
}
