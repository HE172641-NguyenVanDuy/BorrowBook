package com.borrowbook.duyanh.validation.validator;


import com.borrowbook.duyanh.repository.UserRepository;
import com.borrowbook.duyanh.validation.annotation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private UserRepository userRepository; // hoặc UserService nếu dùng service layer

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.isEmpty()) {
            return true; // @NotBlank sẽ xử lý trường hợp này
        }
        return !userRepository.existsByUsername(username); // kiểm tra tính unique
    }

    @Override
    public void initialize(UniqueUsername constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }


}