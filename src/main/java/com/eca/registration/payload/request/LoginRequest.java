package com.eca.registration.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

	@NonNull
	private String username;

	@NonNull
	private String password;

}
