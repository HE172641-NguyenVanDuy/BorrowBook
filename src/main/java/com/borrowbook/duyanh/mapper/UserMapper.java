package com.borrowbook.duyanh.mapper;

import com.borrowbook.duyanh.dto.request.UserDTO;
import com.borrowbook.duyanh.dto.response.UserResponse;
import com.borrowbook.duyanh.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
    User toUser(UserDTO dto);
    UserResponse toUserResponse(User user);
    void updateUser(@MappingTarget User user,UserDTO dto);
}
