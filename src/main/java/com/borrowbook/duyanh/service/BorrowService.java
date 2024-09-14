package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.BorrowDTO;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Borrow;
import com.borrowbook.duyanh.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BorrowService {

    public Borrow borrowingBook(BorrowDTO dto);

    public List<Borrow> getAllBorrowActiveByUserId(int uid);

    public Borrow getBorrowById(int bid);

    public List<Borrow> getHistoryBorrowByUserId(int id);

    PageResponse<Borrow> getBorrowActive(int page, int size, String sortOrder);
}
