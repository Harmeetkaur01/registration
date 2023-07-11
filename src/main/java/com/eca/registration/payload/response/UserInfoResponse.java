package com.eca.registration.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoResponse {
    private String username;
    private String email;
    private String role;
    private Long userId;
    private int flatNo;
    private int towerNo;
    private Long phoneNo;
}
