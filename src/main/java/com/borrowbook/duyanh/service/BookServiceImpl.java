package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.BookCreationRequest;
import com.borrowbook.duyanh.entity.Book;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.BookRepository;
import com.borrowbook.duyanh.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
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
        book.setBookName(request.getBookName());
        book.setReleaseDate(request.getReleaseDate());
        book.setPrice(request.getPrice());
        book.setStatus(request.getStatus());
        book.setAuthor(request.getAuthor());
        book.setQuantity(request.getQuantity());
        book.setCategory(category);

        return bookRepository.saveAndFlush(book);
    }

    @Transactional
    @Override
    public boolean deleteBookById(int id) {
        return bookRepository.deleteBookByBookId(id) > 0;
    }

    @Override
    public boolean activeBookById(int id) {
        return bookRepository.activeBookById(id) > 0;
    }

    @Override
    public List<Book> getAllBookActive() {
        return bookRepository.getAllBookActive();
    }

    @Override
    public List<Book> getAllDeleteBook() {
        return bookRepository.getAllDeleteBook();
    }

    @Override
    public List<Book> getAllBookByCategoryId(int categoryId) {
        return bookRepository.getAllBookByCategoryId(categoryId);
    }
}
