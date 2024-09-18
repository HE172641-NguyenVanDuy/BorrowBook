package com.borrowbook.duyanh.repository;

import com.borrowbook.duyanh.entity.BorrowDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorrowDetailRepository extends JpaRepository<BorrowDetail, Integer> {

    @Query("SELECT b FROM BorrowDetail b WHERE b.borrow.id = :id ")
    List<BorrowDetail> getAllBorrowDetailByBorrowId(@Param("id") int borrowId);

    @Query("SELECT b FROM BorrowDetail b WHERE b.borrow.id = :id AND b.status = 'BORROW' ")
    List<BorrowDetail> getBorrowDetailBorrowByBorrowId(@Param("id") int borrowId);

    @Query("SELECT b FROM BorrowDetail b WHERE b.borrow.id = :id AND b.status = 'RETURNED' ")
    List<BorrowDetail> getBorrowDetailReturnedByBorrowId(@Param("id") int borrowId);

    @Query("UPDATE BorrowDetail b SET b.status = 'RETURNED' WHERE b.id = :id ")
    int returningBook(@Param("id") int borrowId);


}
