package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.UserService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

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
                .message(ErrorCode.USER_CREATED.getMessage())
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



}
