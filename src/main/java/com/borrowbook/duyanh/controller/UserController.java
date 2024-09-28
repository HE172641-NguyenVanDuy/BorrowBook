package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.SearchUserDTO;
import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.dto.response.UserResponse;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.UserService;
import com.borrowbook.duyanh.utils.UtilsExcel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UtilsExcel exportUsersExcel;

    @PreAuthorize("hasAnyRole('ADMIN')")
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

    @PreAuthorize("hasAnyRole('ADMIN')")
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

    @PreAuthorize("hasAnyRole('ADMIN')")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
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

    @PreAuthorize("hasAnyRole('ADMIN')")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/search_user")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchUser(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "username") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestBody SearchUserDTO dto) {

        PageResponse<UserResponse> searchUser = userService.searchUser(page,size,sortBy,sortDirection,dto);
        ApiResponse<PageResponse<UserResponse>> apiResponse = ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(searchUser)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/search_advanced")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> searchAdvancedUser(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sort", defaultValue = "username") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            @RequestBody String keyword) {

        PageResponse<UserResponse> searchUser = userService.searchAdvancedUser(page,size,sortBy,sortDirection,keyword);
        ApiResponse<PageResponse<UserResponse>> apiResponse = ApiResponse.<PageResponse<UserResponse>>builder()
                .code(200)
                .message(ErrorCode.USER_RETRIEVED.getMessage())
                .result(searchUser)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PatchMapping("/active-user/{id}")
    public ResponseEntity<ApiResponse<String>> activeUser(@PathVariable("id") int id) {
        ApiResponse<String> apiResponse = userService.activeUserById(id);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/delete-user/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable("id") int id) {
        ApiResponse<String> apiResponse = userService.deleteUserById(id);
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PatchMapping("/ban-user/{id}")
    public ResponseEntity<ApiResponse<String>> banUser(@PathVariable("id") int id) {
        ApiResponse<String> apiResponse = userService.banUserById(id);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/my-info")
    public ResponseEntity<ApiResponse<User>> getMyInfo() {
        User user = userService.getMyInfo();
        ApiResponse<User> apiResponse = ApiResponse.<User>builder()
                .code(200)
                .result(user)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/export-excel-search")
    public void exportSearchUsersToExcel(HttpServletResponse response, @RequestParam(required = false) String keyword) throws IOException {
        boolean isSearch = (keyword != null && !keyword.trim().isEmpty());
        exportUsersExcel.generateExcel(response, isSearch, keyword);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/import-excel")
    public ResponseEntity<ApiResponse<String>> uploadExcel(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        ApiResponse<String> apiResponse = exportUsersExcel.getTemporaryFileInServer(file,response);
        return ResponseEntity.ok(apiResponse);
    }

}
