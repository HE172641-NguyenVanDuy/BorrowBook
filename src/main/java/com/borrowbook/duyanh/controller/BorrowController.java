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

    @PatchMapping("/return-book/{bdId}")
    public ResponseEntity<ApiResponse<BorrowDetail>> returningBook(
    @PathVariable("bdId") int bdId) {
        if(!borrowDetailService.returningBook( bdId)) {
            throw new  AppException(ErrorCode.ERROR);
        }
        ApiResponse<BorrowDetail> apiResponse = ApiResponse.<BorrowDetail>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
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

    @GetMapping("/get-all-borrow")
    public ResponseEntity<ApiResponse<PageResponse<Borrow>>> getAllBorrow(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                            @RequestParam(value = "size", defaultValue = "2") int size,
                                                                            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder) {
        PageResponse<Borrow> borrows = borrowService.getBorrowActive(page, size, sortOrder);
        ApiResponse<PageResponse<Borrow>> apiResponse = ApiResponse.<PageResponse<Borrow>>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .result(borrows)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
