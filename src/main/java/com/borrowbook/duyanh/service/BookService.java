package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.BookCreationRequest;
import com.borrowbook.duyanh.entity.*;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface BookService {

    Book createdBook(BookCreationRequest request);
    Book getBookById(int id);
    Book updateBook(BookCreationRequest request, int id);
    boolean deleteBookById(int id);
    boolean activeBookById(int id);
    List<Book> getAllBookActive();
    List<Book> getAllDeleteBook();
    List<Book> getAllBookByCategoryId(int categoryId);
}
