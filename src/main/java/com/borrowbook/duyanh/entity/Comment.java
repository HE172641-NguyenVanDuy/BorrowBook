package com.borrowbook.duyanh.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id;

    @Column(name = "comment_date")
    LocalDateTime dateTime;

    @Column(name = "content")
    String content;

    @ManyToOne( cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne( cascade = {
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH
    })
    @JoinColumn(name = "post_id")
    private Post post;
}
