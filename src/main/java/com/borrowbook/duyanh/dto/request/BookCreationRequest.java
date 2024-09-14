package com.borrowbook.duyanh.dto.request;


import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BookCreationRequest {

    @NotBlank(message ="Book's name can not be blank.")
    private String bookName;

    @PastOrPresent(message = "Release date must be in the past or today.")
    private Date releaseDate;

    @DecimalMin(value = "1000.00", message = "The min price is 1000.00 vnđ")
    private BigDecimal price;

    @Positive(message = "Quantity must a positive number.")
    private int quantity;

    @NotBlank(message ="Author of book can not be blank.")
    private String author;

    @NotBlank(message ="Status can not be blank.")
    private String status;

    @NotNull(message ="Category identity can not be blank.")
    @Positive(message = "Category id must positive number.")
    private Integer categoryId;


}
