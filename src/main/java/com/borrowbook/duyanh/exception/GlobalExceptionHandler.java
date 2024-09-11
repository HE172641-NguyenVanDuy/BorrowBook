package com.borrowbook.duyanh.exception;

import com.borrowbook.duyanh.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse<Object>> handlingAppException(AppException exception) {
        ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
                .code(exception.getErrorCode().getCode())
                .message(exception.getErrorCode().getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handlingRuntimeException(RuntimeException exception) {
        ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
                .code(1001)
                .message(exception.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

//    @ExceptionHandler(value = Exception.class)
//    public ResponseEntity<ApiResponse<Object>> handlingException(Exception exception) {
//        ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
//                .code(1003)
//                .message(exception.getMessage())
//                .build();
//        return ResponseEntity.ok(apiResponse);
//    }

}
