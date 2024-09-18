package com.borrowbook.duyanh.service;

import com.borrowbook.duyanh.dto.request.SearchUserDTO;
import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.PageResponse;
import com.borrowbook.duyanh.dto.response.UserResponse;
import com.borrowbook.duyanh.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserResponse getUserById(int id);
    PageResponse<UserResponse> getAllUserActive(int page, int size, String sortBy, String sortDirection);
    PageResponse<UserResponse>  getAllUserDeleted(int page, int size, String sortBy, String sortDirection);
    PageResponse<UserResponse>  getAllUserBanned(int page, int size, String sortBy, String sortDirection);
    boolean activeUserById(int id);
    boolean deleteUserById(int id);
    boolean banUserById(int id);
    UserResponse createUser(UserDTO request);
    UserResponse updateUser(UserDTO request, int id);
    PageResponse<UserResponse> searchUser(int page, int size, String sortBy, String sortDirection, SearchUserDTO dto);
}
