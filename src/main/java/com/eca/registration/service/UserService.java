package com.eca.registration.service;

import com.eca.registration.payload.request.LoginRequest;
import com.eca.registration.payload.request.SignupRequest;
import com.eca.registration.payload.response.MessageResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<MessageResponse> registerUser(SignupRequest signupRequest);

    ResponseEntity<MessageResponse> loginUser(LoginRequest loginRequest);
    ResponseEntity<MessageResponse> logoutUser(Long id, String username);

    ResponseEntity<MessageResponse> fetchUserByFlatNoAndTowerNo(int flatNo, int towerNo);

    ResponseEntity<MessageResponse> fetchUserByUsername(String username);
}
