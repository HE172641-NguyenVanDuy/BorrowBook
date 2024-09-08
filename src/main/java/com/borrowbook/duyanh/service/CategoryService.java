package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.CategoryCreationRequest;
import com.borrowbook.duyanh.entity.Category;
import java.util.List;

public interface CategoryService {

    public Category createCategory(CategoryCreationRequest category);
    public Category updateCategory(CategoryCreationRequest category, int categoryId);
    public List<Category> getAllCategory();
    List<Category> getAllCategoryDelete();
    public Category getCategoryById(int id);
    public boolean deleteCategoryById(int cid);
    public boolean activeCategory(int cid);


}
