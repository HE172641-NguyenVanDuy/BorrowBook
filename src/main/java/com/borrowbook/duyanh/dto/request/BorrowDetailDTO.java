package com.borrowbook.duyanh.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.apache.poi.hpsf.Decimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BorrowDetailDTO {

    @NotNull(message = "BookId is mandatory.")
    int idBook;

    @NotNull(message = "BorrowId is mandatory.")
    int idBorrow;



}
