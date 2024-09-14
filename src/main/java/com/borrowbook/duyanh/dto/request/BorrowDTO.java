package com.borrowbook.duyanh.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BorrowDTO {


//    @Past(message = "Borrow date can not be date in the past.")
//    Date borrowDate;

    @Past(message = "Expiration date can not be date in the past.")
    LocalDate expirationDate;

    @NotNull(message = "Quantity is mandatory.")
    @Positive(message = "Quantity must be positive number.")
    int quantity;

    //    @NotNull(message = "BookId is mandatory.")
    //    int bookId;
    @NotNull(message = "BookIds are mandatory.")
    @Size(min = 1, message = "At least one bookId is required.")
    private List<@NotNull(message = "Each bookId is mandatory.") Integer> bookIds;

    String description;

    @NotNull(message = "UserId is mandatory.")
    int userId;
}
