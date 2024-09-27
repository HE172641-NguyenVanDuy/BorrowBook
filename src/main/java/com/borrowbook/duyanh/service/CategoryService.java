package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.CategoryCreationRequest;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(CategoryCreationRequest category);

    Category updateCategory(CategoryCreationRequest category, int categoryId);

    List<Category> getAllCategory();

    List<Category> getAllCategoryDelete();

    Category getCategoryById(int id);

    ApiResponse<String> deleteCategoryById(int cid);

    ApiResponse<String> activeCategory(int cid);


}
