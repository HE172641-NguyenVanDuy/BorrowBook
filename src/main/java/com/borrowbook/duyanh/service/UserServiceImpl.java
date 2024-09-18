package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.SearchUserDTO;
import com.borrowbook.duyanh.dto.request.UserDTO;
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

    //private final BCryptPasswordEncoder bCrypt  = new BCryptPasswordEncoder(12);

    @Override
    public UserResponse getUserById(int id) {
        return userMapper.toUserResponse( userRepository.findById(id).orElseThrow(
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
    public boolean activeUserById(int id) {
        return userRepository.activeUserById(id) > 0;
    }

    @Override
    @Transactional
    public boolean deleteUserById(int id) {
        return userRepository.deleteUserById(id) > 0;
    }

    @Override
    @Transactional
    public boolean banUserById(int id) {
        return userRepository.banUserById(id) > 0;
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

        User savedUser = userRepository.save(user);
        informationOfUserService.saveInformationOfUser(request,savedUser);

        return UserResponse.fromUser(savedUser);
    }

    @Override
    @Transactional
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
}
