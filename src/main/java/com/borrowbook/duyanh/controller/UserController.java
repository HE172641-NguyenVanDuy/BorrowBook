package com.borrowbook.duyanh.controller;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.entity.Category;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.service.UserService;
import jakarta.validation.Valid;
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
    public List<User> getAllUserActive() {
        return userService.getAllUserActive();
    }

    @GetMapping("get-all-delete")
    public List<User> getAllUserDelete() {
        return userService.getAllUserDeleted();
    }

    @GetMapping("get-all-banned")
    public List<User> getAllUserBanned() {
        return userService.getAllUserBanned();
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
