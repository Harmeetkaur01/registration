package com.eca.registration.payload.request;

import com.eca.registration.model.ERole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {
    @NonNull
    @Size(min = 3, max = 20)
    private String username;
 
    @NonNull
    @Size(max = 50)
    @Email
    private String email;
    
    private ERole role;
    
    @NonNull
    @Size(min = 6, max = 40)
    private String password;

    @NonNull
    private int flatNo;

    @NonNull
    private int towerNo;

    @NonNull
    private Long phoneNo;

}
