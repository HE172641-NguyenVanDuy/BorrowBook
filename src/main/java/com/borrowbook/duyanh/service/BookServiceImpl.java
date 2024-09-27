package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.BookCreationRequest;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Book;
import com.borrowbook.duyanh.entity.BorrowDetail;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.BookRepository;
import com.borrowbook.duyanh.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class BookServiceImpl implements BookService{

    private BookRepository bookRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public BookServiceImpl(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional
    @Override
    public Book createdBook(BookCreationRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
        Book book = new Book();
        book.setBookName(request.getBookName());
        book.setReleaseDate(request.getReleaseDate());
        book.setPrice(request.getPrice());
        book.setStatus(request.getStatus());
        book.setAuthor(request.getAuthor());
        book.setQuantity(request.getQuantity());
        book.setCategory(category);

        return bookRepository.save(book);
    }

    @Override
    public Book getBookById(int id) {
       return bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
    }

    @Transactional
    @Modifying
    @Override
    public Book updateBook(BookCreationRequest request, int id) {
        Book book = getBookById(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage()));
        if( !(request.getBookName().isEmpty())
                && (request.getBookName() != null) ) {
            book.setBookName(request.getBookName());
        }

        if( !(request.getReleaseDate().toString().isEmpty())
                && (request.getReleaseDate() != null) ) {
            book.setReleaseDate(request.getReleaseDate());
        }

        if(request.getPrice().compareTo(BigDecimal.valueOf(1000)) >= 0) {
            book.setPrice(request.getPrice());
        }

        if( !(request.getStatus().isEmpty())
                && (request.getStatus() != null) ) {
            book.setStatus(request.getStatus());
        }

        if( !(request.getAuthor().isEmpty())
                && (request.getAuthor() != null) ) {
            book.setAuthor(request.getAuthor());
        }

        if(request.getQuantity() > 0) {
            book.setQuantity(request.getQuantity());
        }
        book.setCategory(category);

        return bookRepository.saveAndFlush(book);
    }


    @Transactional
    @Override
    public ApiResponse<Book> deleteBookById(int id) {
        boolean isDelete = bookRepository.deleteBookByBookId(id) > 0;
        if(!isDelete) {
            throw new AppException(ErrorCode.ERROR_DELETE);
        }
        return ApiResponse.<Book>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }

    @Override
    public ApiResponse<Book> activeBookById(int id) {
        boolean isActive = bookRepository.activeBookById(id) > 0;
        if(!isActive) {
            throw new AppException(ErrorCode.ERROR);
        }
        return ApiResponse.<Book>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }


    @Override
    public PageResponse<Book> getAllBookActive(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = pagingWithCondition(page,size,sortBy,sortDirection);
        var pageData = bookRepository.getAllBookActive(pageable);
        return PageResponse.<Book>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.stream().toList())
                .build();
    }

    @Override
    public PageResponse<Book> getAllDeleteBook(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = pagingWithCondition(page,size,sortBy,sortDirection);
        var pageData = bookRepository.getAllDeleteBook(pageable);
        return PageResponse.<Book>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.stream().toList())
                .build();
    }

    @Override
    public PageResponse<Book> getAllBookByCategoryId(int page, int size, String sortBy, String sortDirection, int categoryId) {
        Pageable pageable = pagingWithCondition(page,size,sortBy,sortDirection);
        var pageData = bookRepository.getAllBookByCategoryId(categoryId,pageable);
        return PageResponse.<Book>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.stream().toList())
                .build();
    }

    @Override
    public PageResponse<Book> getAllBookByPostId(int page, int size, String sortBy, String sortDirection, int pid) {

        Pageable pageable = pagingWithCondition(page,size,sortBy,sortDirection);
        var pageData = bookRepository.getAllBookByPostId(pid,pageable);
        return PageResponse.<Book>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.stream().toList())
                .build();
    }


    private Pageable pagingWithCondition(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC;
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction,sortBy));
        return pageable;
    }


}
