package com.borrowbook.duyanh.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InformationOfUserDTO {

    @NotBlank(message = "UserId can not be blank.")
    private int userId;

    @NotBlank(message = "Email is mandatory")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^(?:\\+84|0)(?:[1-9]\\d{8})$", message = "Phone number should be valid Vietnamese phone number")
    private String phoneNumber;

    @NotNull(message = "Date of birth is mandatory")
    @Past(message = "Date of birth must be in the past")
    private Date dob;
}
