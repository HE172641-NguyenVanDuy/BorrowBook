package com.borrowbook.duyanh.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CategoryCreationRequest {

    @NotBlank(message = "Category's name can not be blank.")
    @Size(max = 50, message = "Category name must less than 50 characters.")
    private String categoryName;

    @NotBlank(message = "Status of category can not be blank.")
    @Size(max = 50, message = "Category name must less than 50 characters.")
    private String status;
}
