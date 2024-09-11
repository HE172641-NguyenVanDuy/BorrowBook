package com.borrowbook.duyanh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.poi.hpsf.Decimal;

import java.sql.Date;

@Entity
@Table(name = "borrows")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "borrow_date")
    Date borrowDate;

    @Column(name = "expiration_date")
    Date expirationDate;

    @Column(name = "return_date")
    Date returnDate;

    @Column(name = "status")
    String status;

    @Column(name = "total_composation_price")
    Decimal totalCompositionPrice;

}
