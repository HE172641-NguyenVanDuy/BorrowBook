package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.BookCreationRequest;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.Book;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/book")
@Tag(name = "Book Controller")

public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Create new book", description = "API create new book into system.")
    @PostMapping("/created-book")
    public ResponseEntity<ApiResponse<Book>> createdBook(@RequestBody @Valid BookCreationRequest request) {
        Book book = bookService.createdBook(request);
        ApiResponse<Book> apiResponse = ApiResponse.<Book>builder()
                .code(200)
                .message(ErrorCode.BOOK_CREATED.getMessage())
                .result(book)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all")
    public List<Book> getAllBookActive() {
        return bookService.getAllBookActive();
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable("bookId") int id) {
        Book book = bookService.getBookById(id);
        ApiResponse<Book> apiResponse = ApiResponse.<Book>builder()
                .code(200)
                .message(ErrorCode.CATEGORY_RETRIEVED.getMessage())
                .result(book)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update-book/{bookId}")
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable("bookId") int id,@Valid @RequestBody BookCreationRequest request) {
        Book book = bookService.updateBook(request,id);
        ApiResponse<Book> apiResponse = ApiResponse.<Book>builder()
                .code(200)
                .result(book)
                .message(ErrorCode.BOOK_UPDATED.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/delete-book/{bookId}")
    public ResponseEntity<ApiResponse<Book>> deleteBook(@PathVariable("bookId") int bookId) {
        if(!bookService.deleteBookById(bookId)) {
            throw new AppException(ErrorCode.ERROR_DELETE);
        }
        ApiResponse<Book> apiResponse = ApiResponse.<Book>builder()
                .code(200)
                .message(ErrorCode.BOOK_DELETED.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/active-book/{bookId}")
    public ResponseEntity<ApiResponse<Book>> activeBook(@PathVariable("bookId") int bookId) {
        if(!bookService.activeBookById(bookId)) {
            throw new AppException(ErrorCode.ERROR_DELETE);
        }
        ApiResponse<Book> apiResponse = ApiResponse.<Book>builder()
                .code(200)
                .message(ErrorCode.BOOK_ACTIVE.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all-deleted-book")
    public List<Book> getAllDeletedBook() {
        return bookService.getAllDeleteBook();
    }

    @GetMapping("/get-book-by-category/{categoryId}")
    public List<Book> getAllBookByCategoryId(@PathVariable("categoryId") int cId) {
        return bookService.getAllBookByCategoryId(cId);
    }
}
