package com.borrowbook.duyanh.validation.annotation;

import com.borrowbook.duyanh.validation.validator.UniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
    String message() default "Username already exists";
    Class<?>[] groups() default {};

    // Thêm thuộc tính payload nếu cần
    Class<? extends Payload>[] payload() default {};
}
