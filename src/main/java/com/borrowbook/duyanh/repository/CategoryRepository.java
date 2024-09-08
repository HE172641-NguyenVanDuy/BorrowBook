package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.categoryName LIKE :name")
    Category existsCategoryActiveByCategoryName(@Param("name") String categoryName);

    @Query("SELECT c FROM Category c WHERE c.status LIKE 'ACTIVE'")
    List<Category> getAllCategoryActive();

    @Query("SELECT c FROM Category c WHERE c.status LIKE 'DELETE'")
    List<Category> getAllCategoryDelete();

    @Modifying
    @Query("UPDATE Category c SET c.status = 'ACTIVE' WHERE c.id = :cid")
    int activeCategory(@Param("cid") int id);

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.status = 'DELETE' WHERE c.id = :cid")
    int deleteCategory(@Param("cid") int id);

    @Modifying
    @Query("UPDATE Book b SET b.status = 'DELETE' WHERE b.category.id = :cid")
    int deleteBooksByCategory(@Param("cid") int categoryId);
}
