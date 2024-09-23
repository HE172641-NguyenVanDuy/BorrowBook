package com.borrowbook.duyanh.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BorrowDTO {

    @Past(message = "Expiration date can not be date in the past.")
    LocalDate expirationDate;

    @NotNull(message = "Books are mandatory.")
    @Size(min = 1, message = "At least one book is required.")
    private Map<@NotNull(message = "BookId is mandatory.") Integer,
            @NotNull(message = "Quantity is mandatory.")
            @Positive(message = "Quantity must be a positive number.") Integer> books;

    String description;
}
