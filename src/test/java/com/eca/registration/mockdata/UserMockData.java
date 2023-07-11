package com.eca.registration.mockdata;

import com.eca.registration.model.ERole;
import com.eca.registration.model.User;
import com.eca.registration.model.UserSession;
import com.eca.registration.payload.request.LoginRequest;
import com.eca.registration.payload.request.SignupRequest;
import com.eca.registration.payload.response.LoginResponse;
import com.eca.registration.payload.response.MessageResponse;
import com.eca.registration.payload.response.UserInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public class UserMockData {

    public static ResponseEntity<MessageResponse> mockSignUpMessageResponse(){
        return ResponseEntity.ok(MessageResponse.builder().message("User registered successfully!").build());
    }

    public static ResponseEntity<MessageResponse> mockLogoutMessageResponse(){
        return ResponseEntity.ok(MessageResponse.builder().message("You've been signed out!").build());
    }
    public static ResponseEntity<MessageResponse> mockLoginMessageResponse(){
        return ResponseEntity.ok(MessageResponse.builder().body(loginResponse()).build());
    }

    public static ResponseEntity<MessageResponse> mockUserMessageResponse(){
        return ResponseEntity.ok(MessageResponse.builder().body(mockUserResponse()).build());
    }

    public static SignupRequest mockSignupRequest(){
       return SignupRequest.builder()
                .email("test@gmail.com")
                .role(ERole.OWNER)
                .username("TestUser")
                .towerNo(9)
                .flatNo(101)
                .password("test@password")
                .phoneNo(8765482265l).build();
    }

    public static SignupRequest mockSignupEmptyRoleRequest(){
        return SignupRequest.builder()
                .email("test@gmail.com")
                .username("TestUser")
                .towerNo(9)
                .flatNo(101)
                .password("test@password")
                .phoneNo(8765482265l).build();
    }

    public static LoginRequest mockLoginRequest(){
        return LoginRequest.builder()
                .username("TestUser")
                .password("test@password").build();
    }




    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static LoginResponse loginResponse(){
        return LoginResponse.builder()
                .token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJtZWV0IiwiaWF0IjoxNjg3Mjg3OTA1LCJleHAiOjE2ODcyODk3MDV9.kIeiY-UMQVNvdKSDB6T4vW28Tug_8OE7dmzbPhhMQRw").user(UserInfoResponse.builder()
                        .username("TestUser")
                        .email("test@password")
                        .role("OWNER")
                        .userId(1l)
                        .build()).build();
    }

    public static User mockUserResponse(){
        return User.builder()
                .phoneNo(1234567890l)
                .towerNo(9)
                .id(65l)
                .roles(ERole.OWNER)
                .username("TestUser")
                .email("test@gmail.com")
                .flatNo(109)
                .build();
    }

    public static UserSession mockUserSession(){
        return  UserSession.builder()
                .userId(65l)
                .lastLoginTime(new Date())
                .token("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJtZWV0IiwiaWF0IjoxNjg3Mjg3OTA1LCJleHAiOjE2ODcyODk3MDV9.kIeiY-UMQVNvdKSDB6T4vW28Tug_8OE7dmzbPhhMQRw")
                .tokenExpirationTime(new Date(System.currentTimeMillis() + 3600000))
                .build();
    }

}
