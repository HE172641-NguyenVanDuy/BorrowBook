package com.borrowbook.duyanh.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Book")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Book {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "bookname", length = 100)
    private String bookName;

    @Column(name = "release_date")
    @Temporal(TemporalType.DATE)
    private Date releaseDate;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "author")
    private String author;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "category_id")
    private Category category;

}
