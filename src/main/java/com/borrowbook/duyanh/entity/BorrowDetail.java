package com.borrowbook.duyanh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;

@Entity
@Table(name = "borrowdetail")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BorrowDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "borrow_id")
    @ManyToOne( cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "borrow_id")
    Borrow borrow;

    @Column(name = "book_id")
    @ManyToOne( cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "book_id")
    Book book;

    @Column(name = "book_name")
    String bookName;

    @Column(name = "quantity")
    int quantity;

    @Column(name = "composation_price")
    BigDecimal compositionPrice;

    @Column(name = "description")
    String description;

    @Column(name = "status")
    String status;
}
