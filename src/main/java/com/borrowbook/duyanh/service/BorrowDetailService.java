package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.entity.BorrowDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BorrowDetailService {

    boolean returningBook( int bookId);

//    List<BorrowDetail> getAllBorrowDetailByBorrowId( int borrowId);
//
//    List<BorrowDetail> getAllBorrowDetailBorrowingByBorrowId(int borrowId);
}
