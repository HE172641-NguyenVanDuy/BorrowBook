package com.borrowbook.duyanh.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "information_of_user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InformationOfUser {



    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapsId
    @JsonBackReference
    @JoinColumn(name = "user_id") // Tên cột khóa ngoại
    private User user;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "dob")
    private Date dob;

    @Id
    @Column(name = "user_id")
    private int userId;

}
