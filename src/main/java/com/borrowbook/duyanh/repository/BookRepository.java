package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("SELECT b FROM Book b WHERE b.status LIKE 'ACTIVE'")
    List<Book> getAllBookActive();

    @Modifying
    @Query("UPDATE Book c SET c.status = 'DELETE' WHERE c.id = :id")
    int deleteBookByBookId(@Param("id") int id);

    @Query("SELECT b FROM Book b JOIN b.category c WHERE c.id = :cid")
    List<Book> getAllBookByCategoryId(@Param("cid") int categoryId);

    @Query("UPDATE Book c SET c.status = 'ACTIVE' WHERE c.id = :id")
    int activeBookById(@Param("id") int id);

    @Query("SELECT b FROM Book b WHERE b.status LIKE 'DELETE'")
    List<Book> getAllDeleteBook();
}
