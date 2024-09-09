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

    @Id
    @Column(name = "userId")
    private int userId;

    @Column(name = "email")
    private String email;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "dob")
    private Date dob;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Tên cột khóa ngoại
    private User user;
}
