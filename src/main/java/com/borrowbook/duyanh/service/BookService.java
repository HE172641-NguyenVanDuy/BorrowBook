package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.BookCreationRequest;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface BookService {

    Book createdBook(BookCreationRequest request);
    Book getBookById(int id);
    Book updateBook(BookCreationRequest request, int id);
    boolean deleteBookById(int id);
    boolean activeBookById(int id);
    PageResponse<Book> getAllBookActive(int page, int size, String sortBy, String sortDirection);
    PageResponse<Book> getAllDeleteBook(int page, int size, String sortBy, String sortDirection);
    PageResponse<Book> getAllBookByCategoryId(int page, int size, String sortBy, String sortDirection,int categoryId);
    PageResponse<Book> getAllBookByPostId(int page, int size, String sortBy, String sortDirection, int pid);
}
