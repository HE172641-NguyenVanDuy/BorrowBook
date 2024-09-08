package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.entity.Role;
import com.borrowbook.duyanh.entity.User;
import com.borrowbook.duyanh.exception.ErrorCode;
import com.borrowbook.duyanh.repository.RoleRepository;
import com.borrowbook.duyanh.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public User getUserById(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException(ErrorCode.NOT_FOUND.getMessage())
        );
    }

    @Override
    public List<User> getAllUserActive() {
        return userRepository.getAllUserActive();
    }

    @Override
    public List<User> getAllUserDeleted() {
        return userRepository.getAllUserDeleted();
    }

    @Override
    @Transactional
    public List<User> getAllUserBanned() {
        return userRepository.getAllUserBanned();
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
        userRepository.save(user);
        return user;
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
        userRepository.saveAndFlush(user);
        return user;
    }
}
