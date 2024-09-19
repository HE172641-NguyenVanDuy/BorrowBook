package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.SearchUserDTO;
import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.dto.response.UserResponse;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.UserService;
import com.borrowbook.duyanh.utils.ExportUsersExcel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable("id") int id) {
        UserResponse user = userService.getUserById(id);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserDTO userDTO) {
        UserResponse user = userService.createUser(userDTO);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .code(200)
                .message(ErrorCode.USER_CREATED.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/update-user/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestBody @Valid UserDTO userDTO,
                                                                @PathVariable("{userId}") int id) {

        UserResponse user = userService.updateUser(userDTO, id);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .code(200)
                .message(ErrorCode.USER_UPDATED.getMessage())
                .result(user)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all-active")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUserActive(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "username") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        PageResponse<UserResponse> users = userService.getAllUserActive(page, size, sortBy, sortDirection);
        ApiResponse<PageResponse<UserResponse>> apiResponse = ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(users)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all-delete")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUserDelete(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "username") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        PageResponse<UserResponse> users = userService.getAllUserDeleted(page, size, sortBy, sortDirection);
        ApiResponse<PageResponse<UserResponse>> apiResponse = ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(users)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get-all-banned")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUserBanned(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "username") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection) {

        PageResponse<UserResponse> users = userService.getAllUserBanned(page, size, sortBy, sortDirection);
        ApiResponse<PageResponse<UserResponse>> apiResponse = ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(users)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/search_user")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUser(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "username") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestBody SearchUserDTO dto) {

        PageResponse<UserResponse> seachUser = userService.searchUser(page,size,sortBy,sortDirection,dto);
        ApiResponse<PageResponse<UserResponse>> apiResponse = ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(seachUser)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/active-user/{id}")
    public ResponseEntity<ApiResponse<User>> activeUser(@PathVariable("id") int id) {
        if (!userService.activeUserById(id)) {
            throw new AppException(ErrorCode.ERROR);
        }
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable("id") int id) {
        if (!userService.deleteUserById(id)) {
            throw new AppException(ErrorCode.ERROR);
        }
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/ban-user/{id}")
    public ResponseEntity<ApiResponse<User>> banUser(@PathVariable("id") int id) {
        if (!userService.banUserById(id)) {
            throw new AppException(ErrorCode.ERROR);
        }
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

//    @GetMapping("/my-info")
//    public ResponseEntity<ApiResponse<User>> getMyInfo() {
//        User user = userService.getMyInfo();
//        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
//                .code(200)
//                .result(user)
//                .message(ErrorCode.SUCCESS.getMessage())
//                .build();
//        return ResponseEntity.ok(apiResponse);
//    }

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
    public String uploadExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try {
            // Tạo đường dẫn file tạm thời duy nhất
            String tempDir = System.getProperty("java.io.tmpdir");
            String filePath = tempDir + java.io.File.separator + UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Lưu file tạm thời trên server
            java.io.File tempFile = new java.io.File(filePath);
            file.transferTo(tempFile);

            // Gọi phương thức xử lý file Excel
            exportUsersExcel.importExcel(response, filePath);

            // Xóa file tạm thời sau khi xử lý xong
            if (tempFile.exists()) {
                tempFile.delete();
            }
            return "Data has been uploaded successfully.";
        } catch (IOException e) {
            return "Error uploading file: " + e.getMessage();
        }
    }

}
