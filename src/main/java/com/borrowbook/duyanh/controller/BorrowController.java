package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.BorrowDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Borrow;
import com.borrowbook.duyanh.entity.BorrowDetail;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.BorrowDetailService;
import com.borrowbook.duyanh.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @Autowired
    private BorrowDetailService borrowDetailService;


    @PostMapping("/borrowingBook")
    public ResponseEntity<ApiResponse<Borrow>> borrowingBook(@RequestBody @Valid BorrowDTO dto) {
        Borrow borrow = borrowService.borrowingBook(dto);
        ApiResponse<Borrow> apiResponse = ApiResponse.<Borrow>builder()
                .code(200)
                .result(borrow)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-borrow-active/{uid}")
    public ResponseEntity<ApiResponse<List<Borrow>>> getAllBorrowActive(@PathVariable("uid") int uid) {
        List<Borrow> list = borrowService.getAllBorrowActiveByUserId(uid);
        ApiResponse<List<Borrow>> apiResponse = ApiResponse.<List<Borrow>>builder()
                .code(200)
                .result(list)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-borrow-details/{bid}")
    public ResponseEntity<ApiResponse<PageResponse<BorrowDetail>>> getBorrowDetail(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", defaultValue = "BookName") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @PathVariable("uid") int uid) {
        PageResponse<BorrowDetail> list = borrowDetailService.getAllBorrowDetailByBorrowId(page, size, sortBy, sortDirection, uid);
        ApiResponse<PageResponse<BorrowDetail>> apiResponse = ApiResponse.<PageResponse<BorrowDetail>>builder()
                .code(200)
                .result(list)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/return-book/{bdId}")
    public ResponseEntity<ApiResponse<BorrowDetail>> returningBook(@PathVariable("bdId") int bdId) {
        ApiResponse<BorrowDetail> apiResponse = borrowDetailService.returningBook(bdId);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-history-borrow/{uid}")
    public ResponseEntity<ApiResponse<List<Borrow>>> getHistoryBorrowByUserId(@PathVariable("uid") int uid) {
        List<Borrow> list = borrowService.getHistoryBorrowByUserId(uid);
        ApiResponse<List<Borrow>> apiResponse = ApiResponse.<List<Borrow>>builder()
                .code(200)
                .result(list)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-borrow")
    public ResponseEntity<ApiResponse<Borrow>> getAllBorrow() {
        Borrow borrows = borrowService.getBorrowActive();
        ApiResponse<Borrow> apiResponse = ApiResponse.<Borrow>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(borrows)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
