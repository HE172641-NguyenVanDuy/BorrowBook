package com.borrowbook.duyanh.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
    LocalDate borrowDate;

    @Column(name = "expiration_date")
    LocalDate expirationDate;

    @Column(name = "return_date")
    LocalDate returnDate;

    @Column(name = "status")
    String status;

    @Column(name = "total_composation_price")
    BigDecimal totalCompositionPrice;

    @ManyToOne( cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "borrow")
    private List<BorrowDetail> borrowDetails;
}
