package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.Role;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.RoleRepository;
import com.borrowbook.duyanh.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    //private final BCryptPasswordEncoder bCrypt  = new BCryptPasswordEncoder(12);

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );
    }

    @Override
    public PageResponse<User> getAllUserActive(int page, int size, String sortBy, String sortDirection) {
        // Xác định chiều sắp xếp dựa trên tham số
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC; // Giá trị mặc định nếu có lỗi
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction,sortBy));
        var pageData = userRepository.getAllUserActive(pageable);
        return PageResponse.<User>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.stream().toList())
                .build();
    }

    @Override
    public PageResponse<User> getAllUserDeleted(int page, int size, String sortBy, String sortDirection) {
        // Xác định chiều sắp xếp dựa trên tham số
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC; // Giá trị mặc định nếu có lỗi
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction,sortBy));
        var pageData = userRepository.getAllUserDeleted(pageable);
        return PageResponse.<User>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.stream().toList())
                .build();
    }

    @Override
    public PageResponse<User>  getAllUserBanned(int page, int size, String sortBy, String sortDirection) {
        // Xác định chiều sắp xếp dựa trên tham số
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC; // Giá trị mặc định nếu có lỗi
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction,sortBy));
        var pageData = userRepository.getAllUserBanned(pageable);
        return PageResponse.<User>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElement(pageData.getTotalElements())
                .data(pageData.stream().toList())
                .build();
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
    public User createUser(UserDTO request) {
        Role role = roleRepository.findById(request.getRid()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );

        User user = new User();
        user.setRole(role);
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        //user.setPassword(bCrypt.encode(request.getPassword()));
        user.setStatus(request.getStatus().toUpperCase());

        User savedUser = userRepository.save(user);
        informationOfUserService.saveInformationOfUser(request,savedUser);

        return savedUser;
    }

    @Override
    @Transactional
    public User updateUser(UserDTO request, int id) {
        User user = getUserById(id);
        Role role = roleRepository.findById(request.getRid()).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );
        user.setStatus(request.getStatus().toUpperCase());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        //user.setPassword(bCrypt.encode(request.getPassword()));
        user.setRole(role);
        User changedUser = userRepository.saveAndFlush(user);
        informationOfUserService.updateInformationOfUser(request,changedUser);

        return changedUser;
    }
}
