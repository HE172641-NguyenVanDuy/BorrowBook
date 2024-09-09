package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.UserDTO;
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

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );
    }

    @Override
    public Page<User> getAllUserActive(int page, int size, String sortBy, String sortDirection) {
        // Xác định chiều sắp xếp dựa trên tham số
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC; // Giá trị mặc định nếu có lỗi
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sortBy));
        return userRepository.getAllUserActive(pageable);
    }

    @Override
    public Page<User> getAllUserDeleted(int page, int size, String sortBy, String sortDirection) {
        // Xác định chiều sắp xếp dựa trên tham số
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC; // Giá trị mặc định nếu có lỗi
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sortBy));
        return userRepository.getAllUserDeleted(pageable);
    }

    @Override
    public Page<User> getAllUserBanned(int page, int size, String sortBy, String sortDirection) {
        // Xác định chiều sắp xếp dựa trên tham số
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.ASC; // Giá trị mặc định nếu có lỗi
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,sortBy));
        return userRepository.getAllUserBanned(pageable);
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
        user.setStatus(request.getStatus());

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
        user.setStatus(request.getStatus());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(role);

        User changedUser = userRepository.saveAndFlush(user);
        informationOfUserService.updateInformationOfUser(request,changedUser);

        return changedUser;
    }
}
