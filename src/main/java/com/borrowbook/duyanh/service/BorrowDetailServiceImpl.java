package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.dto.response.UserResponse;
import com.borrowbook.duyanh.entity.Book;
import com.borrowbook.duyanh.entity.Borrow;
import com.borrowbook.duyanh.entity.BorrowDetail;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.BookRepository;
import com.borrowbook.duyanh.repository.BorrowDetailRepository;
import com.borrowbook.duyanh.repository.BorrowRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
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
    public ApiResponse<BorrowDetail> returningBook(int id) {
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
        boolean isUpdated = borrowDetailRepository.returningBook(id) > 0;

        if (!isUpdated) {
            throw new AppException(ErrorCode.ERROR);
        }
        return ApiResponse.<BorrowDetail>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(borrowDetail)
                .build();
    }

    @Override
    public PageResponse<BorrowDetail> getAllBorrowDetailByBorrowId(int page, int size, String sortBy, String sortDirection, int borrowId) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC;
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction,sortBy));
        var pageData = borrowDetailRepository.getBorrowDetailActiveByBorrowId(pageable,borrowId);
        return PageResponse.<BorrowDetail>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.getContent())
                .build();
    }

}
