package com.borrowbook.duyanh.dto.request;

import com.borrowbook.duyanh.validation.annotation.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserDTO {

    @NotBlank(message = "User's name can not be bank.")
    @Size(min = 3, max = 50, message = "User's name must be between 3 and 50 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "User's name can only contain alphanumeric characters.")
    @UniqueUsername(message = "Username is already taken.")
    private String username;

    @NotBlank(message = "Password can not be bank.")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters long.")
    private String password;

    @NotNull(message = "Status of user can not be null.")
    private String status;

    @NotNull(message = "Role of user can not be null.")
    private int rid;
}
