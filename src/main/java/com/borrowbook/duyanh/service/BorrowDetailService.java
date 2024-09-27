package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.BorrowDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BorrowDetailService {

    ApiResponse<BorrowDetail> returningBook(int bookId);

    PageResponse<BorrowDetail> getAllBorrowDetailByBorrowId(int page, int size, String sortBy, String sortDirection, int borrowId);

//    List<BorrowDetail> getAllBorrowDetailByBorrowId( int borrowId);
//
//    List<BorrowDetail> getAllBorrowDetailBorrowingByBorrowId(int borrowId);
}
