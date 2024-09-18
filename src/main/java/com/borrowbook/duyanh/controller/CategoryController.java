package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.CategoryCreationRequest;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping("/create-category")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody @Valid CategoryCreationRequest categoryCreationRequest) {
        Category category = categoryService.createCategory(categoryCreationRequest);
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.CATEGORY_CREATED.getMessage())
                .result(category)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/list-categories")
    public List<Category> getAllCategoryActive() {
        return categoryService.getAllCategory();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable("categoryId") int categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.CATEGORY_RETRIEVED.getMessage())
                .result(category)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update-category/{categoryId}")
    public Category updateCategoryById(@Valid @RequestBody CategoryCreationRequest request,
                                       @PathVariable("categoryId") int categoryId) {
        return categoryService.updateCategory(request,categoryId);
    }

    @DeleteMapping("/delete-category/{categoryId}")
    public ResponseEntity<ApiResponse<Category>> deleteCategoryById(@PathVariable("categoryId") int id) {
        if(!categoryService.deleteCategoryById(id)) {
            throw new AppException(ErrorCode.ERROR_DELETE);
        }
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.CATEGORY_DELETED.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("all-delete")
    public List<Category> getAllCategoryDelete() {
        return categoryService.getAllCategoryDelete();
    }

    @PatchMapping("/active-category/{id}")
    public ResponseEntity<ApiResponse<Category>> activeCategory(@PathVariable("id") int id) {
        if(!categoryService.activeCategory(id)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

}
