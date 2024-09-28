package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.SearchUserDTO;
import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.ApiResponse;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.dto.response.UserResponse;
import com.borrowbook.duyanh.entity.Role;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.AppException;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.mapper.UserMapper;
import com.borrowbook.duyanh.repository.RoleRepository;
import com.borrowbook.duyanh.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private InformationOfUserService informationOfUserService;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder bCrypt;

    @Override
    public UserResponse getUserById(int id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        ));
    }

    @Override
    public PageResponse<UserResponse> getAllUserActive(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = pagingDirection(page,size,sortBy,sortDirection);
        var pageData = userRepository.getAllUserActive(pageable);
        List<UserResponse> userResponseList = pageData.stream().map(userMapper::toUserResponse).toList();
        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(userResponseList)
                .build();
    }

    @Override
    public PageResponse<UserResponse> getAllUserDeleted(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = pagingDirection(page,size,sortBy,sortDirection);
        var pageData = userRepository.getAllUserDeleted(pageable);
        List<UserResponse> userResponseList = pageData.stream().map(userMapper::toUserResponse).toList();
        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(userResponseList)
                .build();
    }

    @Override
    public PageResponse<UserResponse>  getAllUserBanned(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = pagingDirection(page,size,sortBy,sortDirection);
        var pageData = userRepository.getAllUserBanned(pageable);
        List<UserResponse> userResponseList = pageData.stream().map(userMapper::toUserResponse).toList();
        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(userResponseList)
                .build();
    }

    private Pageable pagingDirection(int page, int size, String sortBy, String sortDirection) {
        // Xác định chiều sắp xếp dựa trên tham số
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC; // Giá trị mặc định nếu có lỗi
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction,sortBy));
        return pageable;
    }

    @Override
    @Transactional
    public ApiResponse<String> activeUserById(int id) {
        if(!(userRepository.activeUserById(id) > 0)) {
            throw new AppException(ErrorCode.ERROR);
        }
        return ApiResponse.<String>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteUserById(int id) {
        if(!(userRepository.deleteUserById(id) > 0)) {
            throw new AppException(ErrorCode.ERROR);
        }
        return ApiResponse.<String>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }


    @Override
    @Transactional
    public ApiResponse<String> banUserById(int id) {
        if(!(userRepository.banUserById(id) > 0)) {
            throw new AppException(ErrorCode.ERROR);
        }
        return ApiResponse.<String>builder()
                .code(200)
                .message(ErrorCode.SUCCESS.getMessage())
                .build();
    }

    @Override
    @Transactional
    public UserResponse createUser(UserDTO request) {
        Role role = roleRepository.findById(request.getRid()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );

        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(bCrypt.encode(request.getPassword()));
        User savedUser = userRepository.save(user);
        informationOfUserService.saveInformationOfUser(request,savedUser);

        return UserResponse.fromUser(savedUser);
    }

    @Override
    @Transactional
    @Modifying
    public UserResponse updateUser(UserDTO request, int id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );
        Role role = roleRepository.findById(request.getRid()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );
        if(userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        userMapper.updateUser(user,request);
        User changedUser = userRepository.saveAndFlush(user);
        informationOfUserService.updateInformationOfUser(request,changedUser);
        return UserResponse.fromUser(changedUser);
    }

    @Override
    public PageResponse<UserResponse> searchUser(int page,
                                                 int size,
                                                 String sortBy,
                                                 String sortDirection,
                                                 SearchUserDTO dto) {

        Pageable pageable = pagingDirection(page,size,sortBy,sortDirection);
        var pageData = userRepository.searchUsers(dto.getUsername(),
                dto.getPhoneNumber(),
                dto.getEmail(),
                dto.getRoleName(),
                pageable);

        List<UserResponse> userResponseList = pageData.stream().map(userMapper::toUserResponse).toList();
        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(userResponseList)
                .build();
    }

    @Override
    public PageResponse<UserResponse> searchAdvancedUser(int page, int size, String sortBy, String sortDirection, String keyword) {
        Pageable pageable = pagingDirection(page,size,sortBy,sortDirection);
        var pageData =  userRepository.searchAdvancedUser(pageable,keyword);
        List<UserResponse> userResponseList = pageData.stream().map(userMapper::toUserResponse).toList();
        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(userResponseList)
                .build();
    }

    @Override
    public User getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.NOT_FOUND));
        return user;
    }
}
