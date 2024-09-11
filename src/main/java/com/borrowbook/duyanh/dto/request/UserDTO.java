package com.borrowbook.duyanh.dto.request;

import com.borrowbook.duyanh.validation.annotation.Unique;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

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
    @Unique(message = "Username is already taken.")
    private String username;

    @NotBlank(message = "Password can not be bank.")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters long.")
    private String password;

    @NotNull(message = "Status of user can not be null.")
    private String status;

    @NotBlank(message = "Email is mandatory")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email should be valid")
    @Unique(message = "Email is already taken.")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^(?:\\+84|0)(?:[1-9]\\d{8})$", message = "Phone number should be valid Vietnamese phone number")
    @Unique(message = "Phone number is already taken.")
    private String phoneNumber;

    @NotNull(message = "Date of birth is mandatory")
    @Past(message = "Date of birth must be in the past")
    private Date dob;

    @NotNull(message = "Role of user can not be null.")
    private int rid;


}
