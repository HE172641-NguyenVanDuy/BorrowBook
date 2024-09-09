package com.borrowbook.duyanh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "InformationOfUser")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class InformationOfUser {


    @Column(name = "user_id")
    private int userId;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "dob")
    private Date dob;

    @Id
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id") // Tên cột khóa ngoại
    private User user;
}
