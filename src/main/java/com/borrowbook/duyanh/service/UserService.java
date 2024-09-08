package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    User getUserById(int id);
    List<User> getAllUserActive();
    List<User> getAllUserDeleted();
    List<User> getAllUserBanned();
    boolean activeUserById(int id);
    boolean deleteUserById(int id);
    boolean banUserById(int id);
    User createUser(UserDTO request);
    User updateUser(UserDTO request, int id);

}
