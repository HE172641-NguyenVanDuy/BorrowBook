package com.borrowbook.duyanh.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Entity
@Table(name = "CATEGORY")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Category {

    @Id
    @Column(name = "Id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @Column(name = "category_name", length = 50)
    private String categoryName;

    @Column(name = "status", length = 50)
    private String status;

    @JsonIgnore
    @OneToMany(mappedBy = "category")  // `category` là thuộc tính trong lớp Book
    private List<Book> listBooks;

}
