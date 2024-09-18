package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN p.books b WHERE b.id = :bookId AND b.status LIKE 'ACTIVE'")
    Page<Post> getAllPostByBookId(Pageable pageable, @Param("bookId") int bookId);
}
