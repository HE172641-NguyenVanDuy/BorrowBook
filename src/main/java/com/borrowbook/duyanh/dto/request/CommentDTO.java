package com.borrowbook.duyanh.dto.request;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDTO {

    @NotNull(message = "Content is mandatory.")
    String content;

    @NotNull(message = "Post's identify is mandatory.")
    long postId;

    //@NotNull(message = "User's identify is mandatory.")
    int uid;
}
