package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.UserService;
import com.borrowbook.duyanh.utils.ExportUsersExcel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PutMapping("/update-user/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUser(@RequestBody @Valid UserDTO userDTO, @PathVariable("{userId}") int id) {
        User user = userService.updateUser(userDTO, id);
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .message(ErrorCode.USER_UPDATED.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all-active")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getAllUserActive(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                            @RequestParam(value = "size", defaultValue = "2") int size,
                                                                            @RequestParam(value = "sort", defaultValue = "username") String sortBy,
                                                                            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {
        PageResponse<User> users = userService.getAllUserActive(page, size, sortBy, sortDirection);
        ApiResponse<PageResponse<User>> apiResponse = ApiResponse.<PageResponse<User>>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(users)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all-delete")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getAllUserDelete(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                            @RequestParam(value = "size", defaultValue = "2") int size,
                                                                            @RequestParam(value = "sort", defaultValue = "username") String sortBy,
                                                                            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {
        PageResponse<User> users = userService.getAllUserDeleted(page, size, sortBy, sortDirection);
        ApiResponse<PageResponse<User>> apiResponse = ApiResponse.<PageResponse<User>>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(users)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all-banned")
    public ResponseEntity<ApiResponse<PageResponse<User>>> getAllUserBanned(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                            @RequestParam(value = "size", defaultValue = "2") int size,
                                                                            @RequestParam(value = "sort", defaultValue = "username") String sortBy,
                                                                            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {
        PageResponse<User> users = userService.getAllUserBanned(page, size, sortBy, sortDirection);
        ApiResponse<PageResponse<User>> apiResponse = ApiResponse.<PageResponse<User>>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(users)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/active-user/{id}")
    public ResponseEntity<ApiResponse<Category>> activeUser(@PathVariable("id") int id) {
        if (!userService.activeUserById(id)) {
            throw new AppException(ErrorCode.ERROR);
        }
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse<Category>> deleteUser(@PathVariable("id") int id) {
        if (!userService.deleteUserById(id)) {
            throw new AppException(ErrorCode.ERROR);
        }
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/ban-user/{id}")
    public ResponseEntity<ApiResponse<Category>> banUser(@PathVariable("id") int id) {
        if (!userService.banUserById(id)) {
            throw new AppException(ErrorCode.ERROR);
        }
        ApiResponse<Category> apiResponse = ApiResponse.<Category>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping(value = "/export-excel") //
    public void exportUsersToExcel(HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/octet-stream");
            String headerKey = "Content-Disposition";
            String headerValue = "attachment;filename=users.xlsx";
            response.setHeader(headerKey, headerValue);
            exportUsersExcel.generateExcel(response);
        } catch (Exception e) {
            response.reset();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Error exporting users to Excel\"}");
            response.getWriter().flush();
        }
    }

    @PostMapping("/import-excel")
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
