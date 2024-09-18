package com.borrowbook.duyanh.dto.request;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDTO {

    @NotNull(message = "Content is mandatory.")
    String content;

    @NotNull(message = "User's id is mandatory.")
    int uid;
}
