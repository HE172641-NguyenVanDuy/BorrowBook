package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.entity.Borrow;
import com.borrowbook.duyanh.entity.BorrowDetail;
import com.borrowbook.duyanh.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {

    @Query("SELECT b FROM Borrow b WHERE b.status LIKE 'BORROW' AND b.user.id = :uid")
    List<Borrow> getAllBorrowActiveByUserId(@Param("uid") int uid);

    @Query("SELECT bd FROM Borrow bd WHERE bd.expirationDate = :warningDate AND bd.status = 'BORROW'")
    List<Borrow> findBorrowsNearExpiration(@Param("warningDate") LocalDate warningDate);


    @Query("SELECT bd FROM Borrow bd WHERE bd.expirationDate < :today AND bd.status = 'BORROW'")
    List<Borrow> findOverdueBorrows(@Param("today")LocalDate today);

    @Query("SELECT b FROM Borrow b WHERE b.id = :id ")
    List<Borrow> getBorrowByBorrowId(@Param("id") int id);

    @Query("SELECT b FROM Borrow b WHERE b.user.id = :uid")
    List<Borrow> getHistoryBorrowByUserId(@Param("uid") int uid);

    @Query("SELECT COUNT(b) > 0 FROM Borrow b WHERE b.user.id = :uid AND b.status = :status")
    boolean existsByUserAndStatus(@Param("uid") int userId, @Param("status") String status);

    @Query("SELECT b FROM Borrow b WHERE b.status = 'BORROW'")
    Borrow getBorrowActive();

    // lấy danh sách các borrow theo thứ tuwj là ngafy thuee uux nhất

//    @Query(value = "SELECT * FROM Borrow\n" +
//            "WHERE status = 'ACTIVE'\n" +
//            "ORDER BY \n" +
//            "    CASE \n" +
//            "        WHEN MONTH(borrow_date) = MONTH(CURRENT_DATE) AND YEAR(borrow_date) = YEAR(CURRENT_DATE) THEN 0 \n" +
//            "        ELSE 1 \n" +
//            "    END,\n" +
//            "    YEAR(borrow_date) DESC, MONTH(borrow_date) DESC ",
//            nativeQuery = true)
//    Page<Borrow> getBorrowActive(Pageable pageable);

}
