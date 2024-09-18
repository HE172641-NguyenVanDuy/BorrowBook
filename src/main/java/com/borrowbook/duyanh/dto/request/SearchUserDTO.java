package com.borrowbook.duyanh.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchUserDTO {

    private String username;
    private String phoneNumber;
    private String email;
    private String roleName;

}
