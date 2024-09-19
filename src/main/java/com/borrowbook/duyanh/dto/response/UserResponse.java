package com.borrowbook.duyanh.dto.response;

import com.borrowbook.duyanh.entity.InformationOfUser;
import com.borrowbook.duyanh.entity.User;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserResponse {

    String username;
    String roleName;
    String email;
    String phoneNumber;

    public static UserResponse fromUser(User user) {
        // Lấy username và roleName từ đối tượng User và tạo UserResponse
        return new UserResponse(user.getUsername(),
                user.getRole().getRoleName(),
                user.getInformationOfUser().getEmail(),
                user.getInformationOfUser().getPhoneNumber());
    }
}
