package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.UserService;
import com.borrowbook.duyanh.utils.ExportUsersExcel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExportUsersExcel exportUsersExcel;

    @GetMapping("/get-user/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody @Valid UserDTO userDTO) {
        User user = userService.createUser(userDTO);
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .message(ErrorCode.USER_CREATED.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update-user")
    public ResponseEntity<ApiResponse<User>> updateUser(@RequestBody @Valid UserDTO userDTO, int id) {
        User user = userService.getUserById(id);
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .message(ErrorCode.USER_UPDATED.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("get-all-active")
    public ResponseEntity<Page<User>> getAllUserActive(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "2") int size,
                                       @RequestParam(defaultValue = "username") String sortBy,
                                       @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<User> users = userService.getAllUserActive(page,size,sortBy,sortDirection);
        return ResponseEntity.ok(users);
    }

    @GetMapping("get-all-delete")
    public ResponseEntity<Page<User>> getAllUserDelete(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "2") int size,
                                       @RequestParam(defaultValue = "username") String sortBy,
                                       @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<User> users = userService.getAllUserDeleted(page,size,sortBy,sortDirection);
        return ResponseEntity.ok(users);
    }

    @GetMapping("get-all-banned")
    public ResponseEntity<Page<User>> getAllUserBanned(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "2") int size,
                                                       @RequestParam(defaultValue = "username") String sortBy,
                                                       @RequestParam(defaultValue = "asc") String sortDirection) {
        Page<User> users = userService.getAllUserBanned(page,size,sortBy,sortDirection);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/active-user/{id}")
    public ResponseEntity<ApiResponse<Category>> activeUser(@PathVariable("id") int id) {
        if(!userService.activeUserById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse<Category>> deleteUser(@PathVariable("id") int id) {
        if(!userService.deleteUserById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/ban-user/{id}")
    public ResponseEntity<ApiResponse<Category>> banUser(@PathVariable("id") int id) {
        if(!userService.banUserById(id)) {
            throw new AppException(ErrorCode.NOT_FOUND);
        }
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/export-excel")
    public ResponseEntity<?> exportUsersToExcel(HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment;filename=users.xls";
            response.setHeader(headerKey, headerValue);
            exportUsersExcel.generateExcel(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorCode.USER_EXPORT_FAIL);
        }
        return ResponseEntity.ok(ErrorCode.USER_EXPORT);
    }

    @PostMapping("")
    public String uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Tạo đường dẫn file tạm thời duy nhất
            String tempDir = System.getProperty("java.io.tmpdir");
            String filePath = tempDir + UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Kiểm tra quyền truy cập trước khi ghi file
            java.io.File tempFile = new java.io.File(filePath);
            if (tempFile.exists()) {
                return "Error: File already exists or is in use.";
            }

            // Lưu file tạm thời trên server
            file.transferTo(tempFile);
            exportUsersExcel.saveExcelData(filePath);
            return "Data has been uploaded successfully.";
        } catch (IOException e) {
            return "Error uploading file: " + e.getMessage();
        }
    }

}
