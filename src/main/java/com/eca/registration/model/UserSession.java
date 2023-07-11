package com.eca.registration.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_session")
@Data
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    Long userId;

    String token;

    @NonNull
    Date lastLoginTime;
    Date tokenExpirationTime;
}
