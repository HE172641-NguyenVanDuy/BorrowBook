package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.CategoryCreationRequest;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.CategoryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/update-category/{categoryId}")
    public ResponseEntity<ApiResponse<Category>> updateCategoryById(@Valid @RequestBody CategoryCreationRequest request,
                                       @PathVariable("categoryId") int categoryId) {
        Category category = categoryService.updateCategory(request,categoryId);
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .result(category)
                .message(ErrorCode.CATEGORY_DELETED.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/delete-category/{categoryId}")
    public ResponseEntity<ApiResponse<String>> deleteCategoryById(@PathVariable("categoryId") int id) {
        ApiResponse<String> apiResponse = categoryService.deleteCategoryById(id);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/all-delete")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategoryDelete() {
        List<Category> categoryList = categoryService.getAllCategory();
        ApiResponse<List<Category>> apiResponse = ApiResponse.<List<Category>>builder()
                .code(200)
                .result(categoryList)
                .message(ErrorCode.CATEGORY_DELETED.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PatchMapping("/active-category/{id}")
    public ResponseEntity<ApiResponse<String>> activeCategory(@PathVariable("id") int id) {
        ApiResponse<String> apiResponse = categoryService.activeCategory(id);
        return ResponseEntity.ok(apiResponse);
    }

}
