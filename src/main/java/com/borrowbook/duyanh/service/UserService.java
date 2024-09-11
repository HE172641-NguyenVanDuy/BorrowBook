package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    User getUserById(int id);
    PageResponse<User> getAllUserActive(int page, int size, String sortBy, String sortDirection);
    PageResponse<User>  getAllUserDeleted(int page, int size, String sortBy, String sortDirection);
    PageResponse<User>  getAllUserBanned(int page, int size, String sortBy, String sortDirection);
    boolean activeUserById(int id);
    boolean deleteUserById(int id);
    boolean banUserById(int id);
    User createUser(UserDTO request);
    User updateUser(UserDTO request, int id);

}
