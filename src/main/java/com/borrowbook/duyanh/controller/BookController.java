package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.BookCreationRequest;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Book;
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
    public ResponseEntity<ApiResponse<Book>> updateBook(@PathVariable("bookId") int id,
                                                        @Valid @RequestBody BookCreationRequest request) {
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
    public ResponseEntity<ApiResponse<PageResponse<Book>>> getAllDeletedBook(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "BookName") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        PageResponse<Book> books = bookService.getAllDeleteBook(page, size, sortBy, sortDirection);
        ApiResponse<PageResponse<Book>> apiResponse = ApiResponse.<PageResponse<Book>>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(books)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-book-by-category/{categoryId}")
    public ResponseEntity<ApiResponse<PageResponse<Book>>> getAllBookByCategoryId(
            @PathVariable("categoryId") int cId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "BookName") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        PageResponse<Book> books = bookService.getAllBookByCategoryId(page, size, sortBy, sortDirection,cId);
        ApiResponse<PageResponse<Book>> apiResponse = ApiResponse.<PageResponse<Book>>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(books)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-book-by-post/{postId}")
    public ResponseEntity<ApiResponse<PageResponse<Book>>> getAllBookByPostId(
            @PathVariable("postId") int pid,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "BookName") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        PageResponse<Book> books = bookService.getAllBookByPostId(page, size, sortBy, sortDirection,pid);
        ApiResponse<PageResponse<Book>> apiResponse = ApiResponse.<PageResponse<Book>>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(books)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<PageResponse<Book>>> getAllBookActive(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "BookName") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        PageResponse<Book> books = bookService.getAllBookActive(page, size, sortBy, sortDirection);
        ApiResponse<PageResponse<Book>> apiResponse = ApiResponse.<PageResponse<Book>>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(books)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
