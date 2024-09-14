package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.BorrowDTO;
import com.borrowbook.duyanh.entity.Book;
import com.borrowbook.duyanh.entity.Borrow;
import com.borrowbook.duyanh.entity.BorrowDetail;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.BookRepository;
import com.borrowbook.duyanh.repository.BorrowDetailRepository;
import com.borrowbook.duyanh.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowDetailServiceImpl implements BorrowDetailService {

    private final BorrowDetailRepository borrowDetailRepository;

    private final BookRepository bookRepository;

    private final BorrowRepository borrowRepository;

    @Autowired
    public BorrowDetailServiceImpl(BorrowDetailRepository borrowDetailRepository,
                                   BookRepository bookRepository,
                                   BorrowRepository borrowRepository) {
        this.borrowDetailRepository = borrowDetailRepository;
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
    }

    @Override
    public boolean returningBook(int id) {
        LocalDate today = LocalDate.now();
        BorrowDetail borrowDetail = borrowDetailRepository.findById(id)
                .orElseThrow(() ->  new IllegalArgumentException(ErrorCode.ERROR.getMessage()));
        Book book = borrowDetail.getBook();
        book.setQuantity(book.getQuantity() + borrowDetail.getQuantity());
        bookRepository.save(book);

        Borrow borrow = borrowDetail.getBorrow();
        List<BorrowDetail> borrowDetails = borrow.getBorrowDetails();
        boolean allReturned = borrowDetails.stream().allMatch(detail -> detail.getStatus().equals("RETURNED"));

        if (allReturned) {
            borrow.setReturnDate(today);
            borrow.setStatus("RETURNED");
            borrowRepository.save(borrow);
        }
        return borrowDetailRepository.returningBook(id) > 0;
    }

//    @Override
//    public List<BorrowDetail> getAllBorrowDetailByBorrowId(int borrowId) {
//        return borrowDetailRepository.getBorrowDetailBorrowByBorrowId(borrowId);
//    }
//
//    @Override
//    public List<BorrowDetail> getAllBorrowDetailBorrowingByBorrowId(int borrowId) {
//        return borrowDetailRepository.getAllBorrowDetailByBorrowId(borrowId);
//    }
}
